import { element, by, ElementFinder, protractor } from 'protractor';
import { waitUntilDisplayed, waitUntilHidden, isVisible } from '../../util/utils';

const expect = chai.expect;

export default class PlayerUpdatePage {
  pageTitle: ElementFinder = element(by.id('jhipsterSampleApplicationApp.player.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  nameInput: ElementFinder = element(by.css('input#player-name'));
  countryInput: ElementFinder = element(by.css('input#player-country'));
  ageInput: ElementFinder = element(by.css('input#player-age'));
  positionSelect: ElementFinder = element(by.css('select#player-position'));
  footSelect: ElementFinder = element(by.css('select#player-foot'));
  signedInput: ElementFinder = element(by.css('input#player-signed'));
  contractUntilInput: ElementFinder = element(by.css('input#player-contractUntil'));
  valueInput: ElementFinder = element(by.css('input#player-value'));
  teamSelect: ElementFinder = element(by.css('select#player-team'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setNameInput(name) {
    await this.nameInput.sendKeys(name);
  }

  async getNameInput() {
    return this.nameInput.getAttribute('value');
  }

  async setCountryInput(country) {
    await this.countryInput.sendKeys(country);
  }

  async getCountryInput() {
    return this.countryInput.getAttribute('value');
  }

  async setAgeInput(age) {
    await this.ageInput.sendKeys(age);
  }

  async getAgeInput() {
    return this.ageInput.getAttribute('value');
  }

  async setPositionSelect(position) {
    await this.positionSelect.sendKeys(position);
  }

  async getPositionSelect() {
    return this.positionSelect.element(by.css('option:checked')).getText();
  }

  async positionSelectLastOption() {
    await this.positionSelect.all(by.tagName('option')).last().click();
  }
  async setFootSelect(foot) {
    await this.footSelect.sendKeys(foot);
  }

  async getFootSelect() {
    return this.footSelect.element(by.css('option:checked')).getText();
  }

  async footSelectLastOption() {
    await this.footSelect.all(by.tagName('option')).last().click();
  }
  async setSignedInput(signed) {
    await this.signedInput.sendKeys(signed);
  }

  async getSignedInput() {
    return this.signedInput.getAttribute('value');
  }

  async setContractUntilInput(contractUntil) {
    await this.contractUntilInput.sendKeys(contractUntil);
  }

  async getContractUntilInput() {
    return this.contractUntilInput.getAttribute('value');
  }

  async setValueInput(value) {
    await this.valueInput.sendKeys(value);
  }

  async getValueInput() {
    return this.valueInput.getAttribute('value');
  }

  async teamSelectLastOption() {
    await this.teamSelect.all(by.tagName('option')).last().click();
  }

  async teamSelectOption(option) {
    await this.teamSelect.sendKeys(option);
  }

  getTeamSelect() {
    return this.teamSelect;
  }

  async getTeamSelectedOption() {
    return this.teamSelect.element(by.css('option:checked')).getText();
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
    await waitUntilDisplayed(this.saveButton);
    await this.setCountryInput('country');
    await waitUntilDisplayed(this.saveButton);
    await this.setAgeInput('5');
    await waitUntilDisplayed(this.saveButton);
    await this.positionSelectLastOption();
    await waitUntilDisplayed(this.saveButton);
    await this.footSelectLastOption();
    await waitUntilDisplayed(this.saveButton);
    await this.setSignedInput('01/01/2001' + protractor.Key.TAB + '02:30AM');
    await waitUntilDisplayed(this.saveButton);
    await this.setContractUntilInput('01/01/2001' + protractor.Key.TAB + '02:30AM');
    await waitUntilDisplayed(this.saveButton);
    await this.setValueInput('5');
    await this.teamSelectLastOption();
    await this.save();
    await waitUntilHidden(this.saveButton);
  }
}
