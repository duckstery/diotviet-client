/**
 * Print
 *
 * @param {HTMLElement} element
 */
const print = (element) => {
  // Create a print window
  const printWindow = window.open('', '', 'height=500, width=500');
  printWindow.document.write(`<html lang="${env.get('language')}"><body>${element}</body></html>`);
  printWindow.document.close();
  printWindow.onload = () => printWindow.print()
}

/**
 * Build template to actual HTML element
 *
 * @param {string} template
 * @param {[{key: string, sub: array, isIterable: boolean, isParentIterable: boolean, parentKey: string, isIdentifier: boolean}]} tags
 * @param {object} data
 * @return {{element: HTMLElement, print: function}}
 */
const buildPrinter = (template, tags, data) => {


  return {
    element: template,
    print: () => print(element)
  }
}

export {buildPrinter, build}
