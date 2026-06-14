import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('Rental e2e test', () => {
  const rentalPageUrl = '/rental';
  const rentalPageUrlPattern = new RegExp('/rental(\\?.*)?$');
  let username: string;
  let password: string;
  const rentalSample = { startDate: '2026-06-11', endDate: '2026-06-11', status: 'FINISHED' };

  let rental;
  let car;
  let customer;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/cars',
      body: {
        brand: 'incidentally lighthearted gah',
        model: 'gleefully than underachieve',
        productionYear: 21174,
        dailyPrice: 20564.04,
        status: 'AVAILABLE',
      },
    }).then(({ body }) => {
      car = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/customers',
      body: { firstName: 'Jeannie', lastName: 'Kilback', email: 'Miriam88@hotmail.com', phone: '1-748-479-7288 x19408' },
    }).then(({ body }) => {
      customer = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/rentals+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/rentals').as('postEntityRequest');
    cy.intercept('DELETE', '/api/rentals/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/cars', {
      statusCode: 200,
      body: [car],
    });

    cy.intercept('GET', '/api/customers', {
      statusCode: 200,
      body: [customer],
    });
  });

  afterEach(() => {
    if (rental) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/rentals/${rental.id}`,
      }).then(() => {
        rental = undefined;
      });
    }
  });

  afterEach(() => {
    if (car) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/cars/${car.id}`,
      }).then(() => {
        car = undefined;
      });
    }
    if (customer) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/customers/${customer.id}`,
      }).then(() => {
        customer = undefined;
      });
    }
  });

  it('Rentals menu should load Rentals page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('rental');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Rental').should('exist');
    cy.url().should('match', rentalPageUrlPattern);
  });

  describe('Rental page', () => {
    it('should have translated page title', () => {
      cy.visit(rentalPageUrl);
      cy.getEntityHeading('Rental').should('not.contain', 'carrentalApp.rental.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(rentalPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Rental page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/rental/new$'));
        cy.getEntityCreateUpdateHeading('Rental');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', rentalPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/rentals',
          body: {
            ...rentalSample,
            car,
            customer,
          },
        }).then(({ body }) => {
          rental = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/rentals+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/rentals?page=0&size=20>; rel="last",<http://localhost/api/rentals?page=0&size=20>; rel="first"',
              },
              body: [rental],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(rentalPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Rental page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('rental');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', rentalPageUrlPattern);
      });

      it('edit button click should load edit Rental page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Rental');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', rentalPageUrlPattern);
      });

      it('edit button click should load edit Rental page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Rental');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', rentalPageUrlPattern);
      });

      it('last delete button click should delete instance of Rental', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('rental').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', rentalPageUrlPattern);

        rental = undefined;
      });
    });
  });

  describe('new Rental page', () => {
    beforeEach(() => {
      cy.visit(rentalPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Rental');
    });

    it('should create an instance of Rental', () => {
      cy.get(`[data-cy="startDate"]`).type('2026-06-11');
      cy.get(`[data-cy="startDate"]`).blur();
      cy.get(`[data-cy="startDate"]`).should('have.value', '2026-06-11');

      cy.get(`[data-cy="endDate"]`).type('2026-06-12');
      cy.get(`[data-cy="endDate"]`).blur();
      cy.get(`[data-cy="endDate"]`).should('have.value', '2026-06-12');

      cy.get(`[data-cy="totalPrice"]`).type('9602');
      cy.get(`[data-cy="totalPrice"]`).should('have.value', '9602');

      cy.get(`[data-cy="status"]`).select('CANCELLED');

      cy.get(`[data-cy="car"]`).select(1);
      cy.get(`[data-cy="customer"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        rental = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', rentalPageUrlPattern);
    });
  });
});
