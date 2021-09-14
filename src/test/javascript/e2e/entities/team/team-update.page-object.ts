import { element, by, ElementFinder } from 'protractor';
import { waitUntilDisplayed, waitUntilHidden, isVisible } from '../../util/utils';

const expect = chai.expect;

export default class TeamUpdatePage {
  pageTitle: ElementFinder = element(by.id('jhipsterSampleApplicationApp.team.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  nameInput: ElementFinder = element(by.css('input#team-name'));
  leagueSelect: ElementFinder = element(by.css('select#team-league'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setNameInput(name) {
    await this.nameInput.sendKeys(name);
  }

  async getNameInput() {
    return this.nameInput.getAttribute('value');
  }

  async leagueSelectLastOption() {
    await this.leagueSelect.all(by.tagName('option')).last().click();
  }

  async leagueSelectOption(option) {
    await this.leagueSelect.sendKeys(option);
  }

  getLeagueSelect() {
    return this.leagueSelect;
  }

  async getLeagueSelectedOption() {
    return this.leagueSelect.element(by.css('option:checked')).getText();
  }

  async save() {
    await this.saveButton.click();
  }

  async cancel() {
    await this.cancelButton.click();
  }

  getSaveButton() {
    return this.saveButton;
  }

  async enterData() {
    await waitUntilDisplayed(this.saveButton);
    await this.setNameInput('name');
    await this.leagueSelectLastOption();
    await this.save();
    await waitUntilHidden(this.saveButton);
  }
}
