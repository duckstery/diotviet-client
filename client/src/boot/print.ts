// @ts-ignore
import _ from "lodash"
import {util} from "src/boot"
import printJS from 'print-js'

// *************************************************
// Typed
// *************************************************

// Printer shape
export type IPrinter = {
  data: any;
  readonly tags: PrintTag[],
  template: string,
  generators: PrintGenerators,
  generate(): string,
  print(): void
}

// Print tag
export type PrintTag = {
  key: string,
  path: string,
  sub: PrintTag[],
  isIterable: boolean,
  isParentIterable: boolean,
  parentKey: string,
  isIdentifier: boolean
}

// Generator
export type PrintGenerators = { [key: string]: Function }
// Generator content
export type PrintGeneratorContent = {
  content: string
  size?: number
  generators?: PrintGenerators
}

export type GeneratorBuilder = (generators: PrintGenerators, tag: PrintTag, printer: IPrinter) => void

// *************************************************
// Implementation
// *************************************************

export class Printer implements IPrinter {
  private _data: any
  private _isDirty: boolean
  readonly tags: PrintTag[]

  generators: PrintGenerators;
  template: string;

  /**
   * Constructors
   *
   * @param template
   * @param tags
   * @param data
   */
  constructor(template: string, tags: PrintTag[], data: any) {
    this.template = template
    this.tags = tags
    this._data = data
    this._isDirty = false

    this.generators = buildGenerators(this)
  }

  // @ts-ignore
  get data() {
    return this._data
  }

  // @ts-ignore
  set data(value: any) {
    this._isDirty = true
    this._data = value
  }

  generate(): string {
    // Generate a div body for template
    const body = util.div(this.template)
    // Iterate through each generator to generate content for body
    for (const [key, generator] of Object.entries(this.generators)) {
      //
      if (key.includes('items')) {
        continue;
      }
      // Get the PrintableTag element with the id of [key]
      const target = body.querySelector(`#${key}`)
      // Continue to next entry if target is null
      if (util.isUnset(target)) {
        continue;
      }

      // Generate content for target textContent
      // @ts-ignore
      target.innerHTML = generator().content ?? ""
    }

    return body.innerHTML
  }

  print(): void {
    print(this.generate())
  }
}

/**
 * Prepare generator for every map's entry
 *
 * @param {{readonly: {tags: array}, template: string, tags: array, data: object, generate: function, generators: object, print: function}} printer
 * @return {object}
 */
const buildGenerators = (printer: Printer) => Object.values(printer.tags).reduce((generators, tag): PrintGenerators => {
  // Build and add generators
  buildAndAddGenerators(generators, tag, printer)

  return generators
}, {})

/**
 * Build and add generators
 *
 * @param {object} generators
 * @param {{key: string, path: string, sub: array, isIterable: boolean, isParentIterable: boolean, parentKey: string, isIdentifier: boolean}|string} tag
 * @param {{readonly: {tags: array}, template: string, tags: array, data: object, generate: function, generators: object, print: function}} printer
 * @return {function}
 */
const buildAndAddGenerators: GeneratorBuilder = (generators, tag, printer) => {
  if (tag.isIdentifier) {
    // Check if tag is an identifier, build generator for identifier
    buildAndAddGeneratorForIdentifier(generators, tag, printer)
  } else if (tag.isIterable) {
    // Check if tag is iterable, build generator for iterable
    buildAndAddGeneratorForIterable(generators, tag, printer)
  } else if (!_.isEmpty(tag.sub)) {
    // Check if tag has sub, build generator for sub
    buildAndAddGeneratorForSubfields(generators, tag, printer)
  } else {
    // Else, build a normal generator (for those tag with iterable parent, their generators will be built at above step)
    buildAndAddGeneratorForNormal(generators, tag, printer)
  }
}

/**
 * Build content for tag
 *
 * @param {object} generators
 * @param {{key: string, path: string, sub: array, isIterable: boolean, isParentIterable: boolean, parentKey: string, isIdentifier: boolean}} tag
 * @param {{readonly: {tags: array}, template: string, tags: array, data: object, generate: function, generators: object, print: function}} printer
 * @return {function}
 */
const buildAndAddGeneratorForNormal: GeneratorBuilder = (generators, tag, printer) => {
  // Return
  generators[tag.key] = () => util.getProp(printer.data, tag.path)
}

/**
 * Build content for identifier tag
 *
 * @param {object} generators
 * @param {{key: string, path: string, sub: array, isIterable: boolean, isParentIterable: boolean, parentKey: string, isIdentifier: boolean}} tag
 * @param {{readonly: {tags: array}, template: string, tags: array, data: object, generate: function, generators: object, print: function}} printer
 * @return {function}
 */
const buildAndAddGeneratorForIdentifier: GeneratorBuilder = (generators, tag, printer) => {
  // Iterate through each subtag
  tag.sub.forEach(subtag => {
    // Check for generate approach
    if (subtag.key.includes('bc')) {
      console.warn(printer.data)
      // Check if using Barcode
      generators[subtag.key] = () => generateImgElement(printer.data['id'], 'barcode', util.getProp(printer.data, subtag.path))
    } else if (subtag.key.includes('qr')) {
      // Check if using QR
      generators[subtag.key] = () => generateImgElement(printer.data['id'], 'qrcode', util.getProp(printer.data, subtag.path))
    } else {
      // Create normal generator
      generators[subtag.key] = () => util.getProp(printer.data, subtag.path)
    }
  })
}

/**
 * Build content for iterable tag
 *
 * @param {object} generators
 * @param {{key: string, path: string, sub: [{key: string, path: string, sub: array, isIterable: boolean, isParentIterable: boolean, parentKey: string, isIdentifier: boolean}], isIterable: boolean, isParentIterable: boolean, parentKey: string, isIdentifier: boolean}} tag
 * @param {{readonly: {tags: array}, template: string, tags: array, data: object, generate: function, generators: object, print: function}} printer
 * @return {function}
 */
const buildAndAddGeneratorForIterable: GeneratorBuilder = (generators, tag, printer) => {
  // Deep clone tag to make sure changing readonly tag won't change
  const _tag = _.cloneDeep(tag)

  // This generator will show the number of table row should be generated
  generators[tag.key] = () => {
    // Create sub generators
    const subGenerators = {}
    // Get the size of actual data
    const size = util.getProp(printer.data, tag.path).length
    // Template key, path
    let templateKey, templatePath = ''
    // Iterate through each subtag
    _tag.sub.forEach((subtag: PrintTag) => {
      // Save the key
      templateKey = subtag.key
      // Save the path
      templatePath = subtag.path
      // Loop in the size of "size"
      for (let i = 0; i < size; i++) {
        // For each i,
        // Replace subtag key with a key that can be indexed
        subtag.key = templateKey + "_" + i
        // Replace the subtag.path with a corresponding path that point to data
        subtag.path = templatePath.replace('{i}', `${i}`)
        // Add generate for new subtag
        buildAndAddGenerators(subGenerators, subtag, printer)
      }
    })

    return {
      size: size,
      generators: subGenerators
    }
  }
}

/**
 * Build content for sub
 *
 * @param {object} generators
 * @param {{key: string, path: string, sub: array, isIterable: boolean, isParentIterable: boolean, parentKey: string, isIdentifier: boolean}} tag
 * @param {{readonly: {tags: array}, template: string, tags: array, data: object, generate: function, generators: object, print: function}} printer
 * @return {function}
 */
const buildAndAddGeneratorForSubfields: GeneratorBuilder = (generators, tag, printer) => {
  // Build and add generators
  tag.sub.forEach(subtag => buildAndAddGenerators(generators, subtag, printer))
}

/**
 * Generate <img/> for Barcode and QRCode
 *
 * @param {string|number} id
 * @param {string} type
 * @param {string} content
 * @return {string}
 */
const generateImgElement = (id: number | string, type: string, content: string) => {
  return `<div id="${type}" title="${content}"><img src="/api/v1/order/${type}/${id ?? 0}" alt="Scan me!"></div>`
}

/**
 * Print
 *
 * @param {HTMLElement} element
 */
const print = (element: any) => {
  // console.warn(element)
  printJS({printable: element, type: 'raw-html'})
  // Create a print window
  // const printWindow = window.open('', '', 'height=500, width=500');
  // printWindow.document.write(`<html lang="${env.get('language')}"><body>${element}</body></html>`);
  // printWindow.document.close();
  // printWindow.onload = () => printWindow.print()
}

/**
 * Build template to actual HTML element
 *
 * @param {string} template
 * @param {[{key: string, path: string, sub: array, isIterable: boolean, isParentIterable: boolean, parentKey: string, isIdentifier: boolean}]} tags
 * @param {object} data
 * @return {Promise<{readonly: {tags: array}, template: string, tags: array, data: object, generate: Promise<string>, generators: object, print: function()}>}
 */
const buildPrinter = (template: string, tags: PrintTag[], data: any) => {
  // Execute asynchronously
  return util.async(() => {
    // Build and return printer
    return new Printer(template, tags, data)
  });
}

export {buildPrinter, print}
