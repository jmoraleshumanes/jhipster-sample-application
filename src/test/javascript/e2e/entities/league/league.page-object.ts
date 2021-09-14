import { element, by, ElementFinder, ElementArrayFinder } from 'protractor';

import { waitUntilAnyDisplayed, waitUntilDisplayed, click, waitUntilHidden, isVisible } from '../../util/utils';

import NavBarPage from './../../page-objects/navbar-page';

import LeagueUpdatePage from './league-update.page-object';

const expect = chai.expect;
export class LeagueDeleteDialog {
  deleteModal = element(by.className('modal'));
  private dialogTitle: ElementFinder = element(by.id('jhipsterSampleApplicationApp.league.delete.question'));
  private confirmButton = element(by.id('jhi-confirm-delete-league'));

  getDialogTitle() {
    return this.dialogTitle;
  }

  async clickOnConfirmButton() {
    await this.confirmButton.click();
  }
}

export default class LeagueComponentsPage {
  createButton: ElementFinder = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('div table .btn-danger'));
  title: ElementFinder = element(by.id('league-heading'));
  noRecords: ElementFinder = element(by.css('#app-view-container .table-responsive div.alert.alert-warning'));
  table: ElementFinder = element(by.css('#app-view-container div.table-responsive > table'));

  records: ElementArrayFinder = this.table.all(by.css('tbody tr'));

  getDetailsButton(record: ElementFinder) {
    return record.element(by.css('a.btn.btn-info.btn-sm'));
  }

  getEditButton(record: ElementFinder) {
    return record.element(by.css('a.btn.btn-primary.btn-sm'));
  }

  getDeleteButton(record: ElementFinder) {
    return record.element(by.css('a.btn.btn-danger.btn-sm'));
  }

  async goToPage(navBarPage: NavBarPage) {
    await navBarPage.getEntityPage('league');
    await waitUntilAnyDisplayed([this.noRecords, this.table]);
    return this;
  }

  async goToCreateLeague() {
    await this.createButton.click();
    return new LeagueUpdatePage();
  }

  async deleteLeague() {
    const deleteButton = this.getDeleteButton(this.records.last());
    await click(deleteButton);

    const leagueDeleteDialog = new LeagueDeleteDialog();
    await waitUntilDisplayed(leagueDeleteDialog.deleteModal);
    expect(await leagueDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/jhipsterSampleApplicationApp.league.delete.question/);
    await leagueDeleteDialog.clickOnConfirmButton();

    await waitUntilHidden(leagueDeleteDialog.deleteModal);

    expect(await isVisible(leagueDeleteDialog.deleteModal)).to.be.false;
  }
}
