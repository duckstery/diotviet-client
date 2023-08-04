import _ from "lodash"
import {util} from "boot/util";

/**
 * Analyze tag key to get self and field key
 *
 * @param {string} key
 * @return {string[]}
 */
const analyzeKey = (key) => {
  return key.split('_')
}

/**
 * Flatten list of tags and its children <br>
 * Map through each tag and get a flat array
 *
 * @param {[{key: string, sub: array, isIterable: boolean, isParentIterable: boolean, parentKey: string, isIdentifier: boolean}]} tags
 * @return {[{key: string, sub: array, isIterable: boolean, isParentIterable: boolean, parentKey: string, isIdentifier: boolean}]}
 */
const flattenTags = (tags) => _.isEmpty(tags) ? [] : _.flatMap(tags, (tag) => {
  // Convert tag.sub to array of key and flatten tag.sub
  return [{...tag, sub: tag.sub.map(t => t.key)}, ...flattenTags(tag.sub)]
})

/**
 * Prepare generator for every map's entry
 *
 * @param {[{key: string, sub: array, isIterable: boolean, isParentIterable: boolean, parentKey: string, isIdentifier: boolean}]} tags
 * @param {{data: object}} dataRef
 * @return {object}
 */
const buildGenerators = (tags, dataRef) => Object.values(tags).reduce((generators, tag) => {
  if (tag.isIdentifier) {
    // Check if tag is an identifier, build generator for identifier
    buildAndAddGeneratorForIdentifier(generators, tag, dataRef)
  } else if (tag.isIterable) {
    // Check if tag is iterable, build generator for iterable
    buildAndAddGeneratorForIterable(generators, tag, dataRef)
  } else if (!tag.isParentIterable) {
    // Else, build a normal generator (for those tag with iterable parent, their generators will be built at above step)
    buildAndAddGenerator(generators, tag, dataRef)
  }

  return generators
}, {})

/**
 * Build content for tag
 *
 * @param {object} generators
 * @param {{key: string, sub: array, isIterable: boolean, isParentIterable: boolean, parentKey: string, isIdentifier: boolean}|string} tag
 * @param {{data: object}} dataRef
 * @return {function}
 */
const buildAndAddGenerator = (generators, tag, dataRef) => {
  // Get key
  const key = typeof tag === 'string' ? tag : tag.key
  // Analyze key
  const [_, field] = analyzeKey(key)
  // Return
  generators[key] = () => dataRef.data[field]
}

/**
 * Build content for identifier tag
 *
 * @param {object} generators
 * @param {{key: string, sub: array, isIterable: boolean, isParentIterable: boolean, parentKey: string, isIdentifier: boolean}} tag
 * @param {{data: object}} dataRef
 * @return {function}
 */
const buildAndAddGeneratorForIdentifier = (generators, tag, dataRef) => {
  // Get dataRef key by getting parentKey of tag
  let dataKey = tag.parentKey
  // Check if parentKey is empty
  if (dataKey === '' || util.isUnset(dataKey)) {
    // In this case, use tag key instead
    dataKey = tag.key
  }
  // Analyze key
  const [_, field] = analyzeKey(dataKey)

  // Check for generate approach
  if (tag.key.includes('bc')) {
    // Check if using Barcode
    generators[tag.key] = () => generateBarcodeElement(dataRef.data['id'], dataRef.data[field])
  } else if (tag.key.includes('qr')) {
    // Check if using QR
    generators[tag.key] = () => generateQRCodeElement(dataRef.data['id'], dataRef.data[field])
  } else {
    // Create normal generator
    buildAndAddGenerator(generators, dataKey, dataRef)
  }
}

/**
 * Build content for iterable tag
 *
 * @param {object} generators
 * @param {{key: string, sub: array, isIterable: boolean, isParentIterable: boolean, parentKey: string, isIdentifier: boolean}} tag
 * @param {{data: object}} dataRef
 * @return {function}
 */
const buildAndAddGeneratorForIterable = (generators, tag, dataRef) => {
  // Analyze parent key
  const [_, parentField] = analyzeKey(tag.key)
  // Iterate through each sub key
  const subGenerators = tag.sub.reduce((subGens, subKey) => {
    // Analyze sub key
    const [_, subField] = analyzeKey(subKey)
    // Iterate through each sub data in parentField
    // Then create a generator to generate subData[subField]
    dataRef.data[parentField].forEach((subData, index) => subGens[`${subField}_${index}`] = () => subData[subField])

    return subGens;
  }, {})

  // This generator will show the number of table row should be generated
  generators[tag.key] = () => ({
    size: tag.sub.length,
    generators: subGenerators
  })
}

/**
 * Generate Barcode as SVG
 *
 * @param {string|number} id
 * @return {string}
 */
const generateBarcodeElement = (id, content) => {
  return `<div id="barcode" title="${content}"><img src="/v1/order/barcode/${id ?? 0}" style="display: block;" alt="Scan me!"></div>`
}

/**
 * Generate QRCode as span
 *
 * @param {string|number} id
 * @return {string}
 */
const generateQRCodeElement = (id, content) => {
  return `<div id="qrcode" title="${content}"><img src="/v1/order/qrcode/${id ?? 0}" style="display: block;" alt="Scan me!"></div>`
}

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
 * @param {boolean} clone
 * @return {{template: string, tags: array, data: object, element: HTMLElement, print: function}}
 */
const buildPrinter = (template, tags, data, clone = false) => {
  // Build printer
  const printer = {
    readonly: {
      template: template,
      tags: _.cloneDeep(tags),
    },
    generators: {},
    data: data,
    element: '',
    print: () => print(this.element)
  }

  // Execute asynchronously
  util.async(() => {
    // Flatten tags
    const flatTags = flattenTags(tags)
    // Prepare generator for every map's entry
    printer.generators = buildGenerators(flatTags, printer)
    console.warn(tags)
    Object.entries(printer.generators).map(([key, value]) => {
      if (!key.includes('items')) {
        console.warn(`${key}: ${value()}`)
      }
    })
  });

  return printer
}

export {buildPrinter, print}
