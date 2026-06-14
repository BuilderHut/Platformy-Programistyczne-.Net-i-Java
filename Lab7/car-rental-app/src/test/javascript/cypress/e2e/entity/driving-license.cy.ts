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

describe('DrivingLicense e2e test', () => {
  const drivingLicensePageUrl = '/driving-license';
  const drivingLicensePageUrlPattern = new RegExp('/driving-license(\\?.*)?$');
  let username: string;
  let password: string;
  const drivingLicenseSample = { licenseNumber: 'critical yippee rout', issueDate: '2026-06-11', expirationDate: '2026-06-11' };

  let drivingLicense;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/driving-licenses+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/driving-licenses').as('postEntityRequest');
    cy.intercept('DELETE', '/api/driving-licenses/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (drivingLicense) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/driving-licenses/${drivingLicense.id}`,
      }).then(() => {
        drivingLicense = undefined;
      });
    }
  });

  it('DrivingLicenses menu should load DrivingLicenses page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('driving-license');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('DrivingLicense').should('exist');
    cy.url().should('match', drivingLicensePageUrlPattern);
  });

  describe('DrivingLicense page', () => {
    it('should have translated page title', () => {
      cy.visit(drivingLicensePageUrl);
      cy.getEntityHeading('DrivingLicense').should('not.contain', 'carrentalApp.drivingLicense.home.title');
    });

    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(drivingLicensePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create DrivingLicense page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/driving-license/new$'));
        cy.getEntityCreateUpdateHeading('DrivingLicense');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', drivingLicensePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/driving-licenses',
          body: drivingLicenseSample,
        }).then(({ body }) => {
          drivingLicense = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/driving-licenses+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/driving-licenses?page=0&size=20>; rel="last",<http://localhost/api/driving-licenses?page=0&size=20>; rel="first"',
              },
              body: [drivingLicense],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(drivingLicensePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details DrivingLicense page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('drivingLicense');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', drivingLicensePageUrlPattern);
      });

      it('edit button click should load edit DrivingLicense page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('DrivingLicense');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', drivingLicensePageUrlPattern);
      });

      it('edit button click should load edit DrivingLicense page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('DrivingLicense');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', drivingLicensePageUrlPattern);
      });

      it('last delete button click should delete instance of DrivingLicense', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('drivingLicense').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', drivingLicensePageUrlPattern);

        drivingLicense = undefined;
      });
    });
  });

  describe('new DrivingLicense page', () => {
    beforeEach(() => {
      cy.visit(drivingLicensePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('DrivingLicense');
    });

    it('should create an instance of DrivingLicense', () => {
      cy.get(`[data-cy="licenseNumber"]`).type('gee');
      cy.get(`[data-cy="licenseNumber"]`).should('have.value', 'gee');

      cy.get(`[data-cy="issueDate"]`).type('2026-06-12');
      cy.get(`[data-cy="issueDate"]`).blur();
      cy.get(`[data-cy="issueDate"]`).should('have.value', '2026-06-12');

      cy.get(`[data-cy="expirationDate"]`).type('2026-06-11');
      cy.get(`[data-cy="expirationDate"]`).blur();
      cy.get(`[data-cy="expirationDate"]`).should('have.value', '2026-06-11');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        drivingLicense = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', drivingLicensePageUrlPattern);
    });
  });
});
