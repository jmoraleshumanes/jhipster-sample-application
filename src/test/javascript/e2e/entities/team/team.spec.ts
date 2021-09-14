import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import TeamComponentsPage from './team.page-object';
import TeamUpdatePage from './team-update.page-object';
import {
  waitUntilDisplayed,
  waitUntilAnyDisplayed,
  click,
  getRecordsCount,
  waitUntilHidden,
  waitUntilCount,
  isVisible,
} from '../../util/utils';

const expect = chai.expect;

describe('Team e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let teamComponentsPage: TeamComponentsPage;
  let teamUpdatePage: TeamUpdatePage;
  const username = process.env.E2E_USERNAME ?? 'admin';
  const password = process.env.E2E_PASSWORD ?? 'admin';

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.waitUntilDisplayed();
    await signInPage.username.sendKeys(username);
    await signInPage.password.sendKeys(password);
    await signInPage.loginButton.click();
    await signInPage.waitUntilHidden();
    await waitUntilDisplayed(navBarPage.entityMenu);
    await waitUntilDisplayed(navBarPage.adminMenu);
    await waitUntilDisplayed(navBarPage.accountMenu);
  });

  beforeEach(async () => {
    await browser.get('/');
    await waitUntilDisplayed(navBarPage.entityMenu);
    teamComponentsPage = new TeamComponentsPage();
    teamComponentsPage = await teamComponentsPage.goToPage(navBarPage);
  });

  it('should load Teams', async () => {
    expect(await teamComponentsPage.title.getText()).to.match(/Teams/);
    expect(await teamComponentsPage.createButton.isEnabled()).to.be.true;
  });

  it('should create and delete Teams', async () => {
    const beforeRecordsCount = (await isVisible(teamComponentsPage.noRecords)) ? 0 : await getRecordsCount(teamComponentsPage.table);
    teamUpdatePage = await teamComponentsPage.goToCreateTeam();
    await teamUpdatePage.enterData();
    expect(await isVisible(teamUpdatePage.saveButton)).to.be.false;

    expect(await teamComponentsPage.createButton.isEnabled()).to.be.true;
    await waitUntilDisplayed(teamComponentsPage.table);
    await waitUntilCount(teamComponentsPage.records, beforeRecordsCount + 1);
    expect(await teamComponentsPage.records.count()).to.eq(beforeRecordsCount + 1);

    await teamComponentsPage.deleteTeam();
    if (beforeRecordsCount !== 0) {
      await waitUntilCount(teamComponentsPage.records, beforeRecordsCount);
      expect(await teamComponentsPage.records.count()).to.eq(beforeRecordsCount);
    } else {
      await waitUntilDisplayed(teamComponentsPage.noRecords);
    }
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
