import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import PlayerComponentsPage from './player.page-object';
import PlayerUpdatePage from './player-update.page-object';
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

describe('Player e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let playerComponentsPage: PlayerComponentsPage;
  let playerUpdatePage: PlayerUpdatePage;
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
    playerComponentsPage = new PlayerComponentsPage();
    playerComponentsPage = await playerComponentsPage.goToPage(navBarPage);
  });

  it('should load Players', async () => {
    expect(await playerComponentsPage.title.getText()).to.match(/Players/);
    expect(await playerComponentsPage.createButton.isEnabled()).to.be.true;
  });

  it('should create and delete Players', async () => {
    const beforeRecordsCount = (await isVisible(playerComponentsPage.noRecords)) ? 0 : await getRecordsCount(playerComponentsPage.table);
    playerUpdatePage = await playerComponentsPage.goToCreatePlayer();
    await playerUpdatePage.enterData();
    expect(await isVisible(playerUpdatePage.saveButton)).to.be.false;

    expect(await playerComponentsPage.createButton.isEnabled()).to.be.true;
    await waitUntilDisplayed(playerComponentsPage.table);
    await waitUntilCount(playerComponentsPage.records, beforeRecordsCount + 1);
    expect(await playerComponentsPage.records.count()).to.eq(beforeRecordsCount + 1);

    await playerComponentsPage.deletePlayer();
    if (beforeRecordsCount !== 0) {
      await waitUntilCount(playerComponentsPage.records, beforeRecordsCount);
      expect(await playerComponentsPage.records.count()).to.eq(beforeRecordsCount);
    } else {
      await waitUntilDisplayed(playerComponentsPage.noRecords);
    }
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
