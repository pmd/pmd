import { LightningElement, api, wire, track } from 'lwc';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';

export default class InteractionScore extends LightningElement {
  @api recordId;
  @track
  relatedRecords;
  @track showMessage;
  /**
   * @description This is a wire adapter used to get case Object Info
   * @param
   */
  @wire(getRelatedRecordByCaseId, {
    caseId: '$recordId'
  })
  caseObjectInfo({
    error,
    data
  }) {
    if (data) {

    }
    handleDetailAnalysisClick(event) {
    }
    /**
     * @description Method to show toast incase of any error.
     */
    showErrorToast(errorMessage) {
      const evt = new ShowToastEvent({
        title: 'Error Occurred',
        message: errorMessage,
        variant: 'error'
      });
      this.dispatchEvent(evt);
    }
  }
}