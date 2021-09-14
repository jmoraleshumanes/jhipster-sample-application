import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import LeagueComponentsPage from './league.page-object';
import LeagueUpdatePage from './league-update.page-object';
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

describe('League e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let leagueComponentsPage: LeagueComponentsPage;
  let leagueUpdatePage: LeagueUpdatePage;
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
    leagueComponentsPage = new LeagueComponentsPage();
    leagueComponentsPage = await leagueComponentsPage.goToPage(navBarPage);
  });

  it('should load Leagues', async () => {
    expect(await leagueComponentsPage.title.getText()).to.match(/Leagues/);
    expect(await leagueComponentsPage.createButton.isEnabled()).to.be.true;
  });

  it('should create and delete Leagues', async () => {
    const beforeRecordsCount = (await isVisible(leagueComponentsPage.noRecords)) ? 0 : await getRecordsCount(leagueComponentsPage.table);
    leagueUpdatePage = await leagueComponentsPage.goToCreateLeague();
    await leagueUpdatePage.enterData();
    expect(await isVisible(leagueUpdatePage.saveButton)).to.be.false;

    expect(await leagueComponentsPage.createButton.isEnabled()).to.be.true;
    await waitUntilDisplayed(leagueComponentsPage.table);
    await waitUntilCount(leagueComponentsPage.records, beforeRecordsCount + 1);
    expect(await leagueComponentsPage.records.count()).to.eq(beforeRecordsCount + 1);

    await leagueComponentsPage.deleteLeague();
    if (beforeRecordsCount !== 0) {
      await waitUntilCount(leagueComponentsPage.records, beforeRecordsCount);
      expect(await leagueComponentsPage.records.count()).to.eq(beforeRecordsCount);
    } else {
      await waitUntilDisplayed(leagueComponentsPage.noRecords);
    }
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
