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
  generate(): Element | null,
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
export type PrintGenerator = () => PrintGeneratorResource
export type PrintGenerators = { [key: string]: PrintGenerator }
// Generator content
export type PrintGeneratorResource = {
  content?: string
  size?: number
  generators?: PrintGenerators
}

// *************************************************
// Implementation
// *************************************************

export class Printer implements IPrinter {
  private _data: any
  private _isDirty: boolean
  private _element: Element | null

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
    this._data = data
    this._isDirty = false
    this._element = null

    this.template = template
    this.tags = tags

    this.generators = this._buildGenerators()
  }

  /**
   * Getter
   */
  get data() {
    return this._data
  }

  /**
   * Setter
   *
   * @param value
   */
  set data(value: any) {
    this._isDirty = true
    this._data = value
  }

  /**
   * Generate printed Element
   */
  generate(): Element | null {
    // Check if is not dirty, return cached element
    if (!this._isDirty && this._element !== null) {
      return this._element
    }

    // Generate a div body for template
    this._element = util.div(this.template)

    // Iterate through each simple printable tag in the printed Element
    this._element.querySelectorAll('.simple-tag')
      .forEach(el => this._generateAndSetContentForElement(el, this.generators))

    // Generate iterable tag
    this._generateAndSetContentForWrappingTable(this._element, this.generators)

    return this._element
  }

  /**
   * Print the printed Element
   */
  print(): void {
    print(this.generate())
  }

  // ************************
  // Private
  // ************************

  /**
   * Prepare generator for every map's entry
   *
   * @return {PrintGenerators}
   */
  private _buildGenerators(): PrintGenerators {
    return Object.values(this.tags).reduce((generators, tag): PrintGenerators => {
      // Build and add generators
      this._buildAndAddGenerators(generators, tag)

      return generators
    }, {})
  }

  /**
   * Build and add generators
   *
   * @param {object} generators
   * @param {PrintTag|string} tag
   */
  private _buildAndAddGenerators(generators: PrintGenerators, tag: PrintTag) {
    if (tag.isIdentifier) {
      // Check if tag is an identifier, build generator for identifier
      this._buildAndAddGeneratorForIdentifier(generators, tag)
    } else if (tag.isIterable) {
      // Check if tag is iterable, build generator for iterable
      this._buildAndAddGeneratorForIterable(generators, tag)
    } else if (!_.isEmpty(tag.sub)) {
      // Check if tag has sub, build generator for sub
      this._buildAndAddGeneratorForSubfields(generators, tag)
    } else {
      // Else, build a normal generator (for those tag with iterable parent, their generators will be built at above step)
      this._buildAndAddGeneratorForNormal(generators, tag)
    }
  }

  /**
   * Build a PrintGenerator
   *
   * @param content
   * @param size
   * @param generators
   */
  private _buildGenerator(content?: string, size?: number, generators?: PrintGenerators): PrintGenerator {
    return (): PrintGeneratorResource => ({
      content: content,
      size: size,
      generators: generators
    })
  }

  /**
   * Build content for tag
   *
   * @param {PrintGenerators} generators
   * @param {PrintTag} tag
   */
  private _buildAndAddGeneratorForNormal(generators: PrintGenerators, tag: PrintTag) {
    // Return
    generators[tag.key] = this._buildGenerator(util.getProp(this._data, tag.path))
  }

  /**
   * Build content for identifier tag
   *
   * @param {PrintGenerators} generators
   * @param {PrintTag} tag
   */
  private _buildAndAddGeneratorForIdentifier(generators: PrintGenerators, tag: PrintTag) {
    // Iterate through each subtag
    tag.sub.forEach(subtag => {
      // Check for generate approach
      if (subtag.key.includes('bc')) {
        // Check if using Barcode
        generators[subtag.key] = this._buildGenerator(this._generateImgElement(this._data['id'], 'barcode', util.getProp(this._data, subtag.path)))
      } else if (subtag.key.includes('qr')) {
        // Check if using QR
        generators[subtag.key] = this._buildGenerator(this._generateImgElement(this._data['id'], 'qrcode', util.getProp(this._data, subtag.path)))
      } else {
        // Create normal generator
        generators[subtag.key] = this._buildGenerator(util.getProp(this._data, subtag.path))
      }
    })
  }

  /**
   * Build content for iterable tag
   *
   * @param {PrintGenerators} generators
   * @param {PrintTag} tag
   */
  private _buildAndAddGeneratorForIterable(generators: PrintGenerators, tag: PrintTag) {
    // This generator will show the number of table row should be generated
    generators[tag.key] = () => {
      // Deep clone tag to make sure changing readonly tag won't change
      const _tag: PrintTag = _.cloneDeep(tag)
      // Create sub generators
      const subGenerators = {}
      // Get the size of actual data
      const size = util.getProp(this._data, tag.path).length
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
          this._buildAndAddGenerators(subGenerators, subtag)
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
   * @param {PrintGenerators} generators
   * @param {PrintTag} tag
   */
  private _buildAndAddGeneratorForSubfields(generators: PrintGenerators, tag: PrintTag) {
    // Build and add generators
    tag.sub.forEach(subtag => this._buildAndAddGenerators(generators, subtag))
  }

  /**
   * Get generator by element
   *
   * @param element
   * @param generators
   * @param index
   * @private
   */
  private _getGeneratorForElement(element: Element, generators: PrintGenerators | undefined, index: number = -1): PrintGenerator {
    // Get generator key
    const key = element.id.substring(39) + (index >= 0 ? `_${index}` : '')
    // Check if generator for this key is exists
    if (generators !== undefined && typeof generators[key] === 'function') {
      return generators[key]
    }

    return () => ({content: undefined})
  }

  /**
   * Generate <img/> for Barcode and QRCode
   *
   * @param {string|number} id
   * @param {string} type
   * @param {string} content
   * @return {string}
   */
  private _generateImgElement(id: number | string, type: string, content: string) {
    return `<div id="${type}" title="${content}"><img src="/api/v1/order/${type}/${id ?? 0}" alt="Scan me!"></div>`
  }

  /**
   * Generate content with generator and set content for Element
   *
   * @param element
   * @param generators
   * @param index
   */
  private _generateAndSetContentForElement(element: Element, generators: PrintGenerators | undefined, index: number = -1) {
    // Pick generator by key
    const generator = this._getGeneratorForElement(element, generators, index)
    // Generate resource
    const resource = generator()
    // Set el innerHTML with content.content
    element.innerHTML = resource.content ?? ''
  }

  /**
   * Handle iterable printable tag (wrapping table)
   *
   * @private
   */
  private _generateAndSetContentForWrappingTable(element: Element, generators: PrintGenerators | undefined, times: number = 0) {
    // Break condition
    if (element === null || times === 5) return

    // Iterate through each wrapping-table, we'll presume that every wrapping-table will have a tbody
    element.querySelectorAll('.wrapping-table > tbody').forEach(tbody => {
      // Save childNodes (that is not a header) as template
      const templateElements = Array.from(tbody.children)
        .filter(el => util.isUnset(el.querySelector('th')) && !util.isUnset(tbody.removeChild(el)))
      // Pick generator by table.id (<tbody>'s parent) and generate content
      // @ts-ignore
      const resource = generators[tbody.parentElement.id]()

      // Loop
      for (let i = 0; i < (resource.size ?? 0); i++) {
        // Iterate through each templated element
        for (const el of templateElements) {
          // Clone the el
          // @ts-ignore
          const cloned = <Element>el.cloneNode(true)
          // Query all .iterable-tag and iterate through each of them
          cloned.querySelectorAll('.iterable-tag')
            .forEach((tag: Element) => this._generateAndSetContentForElement(tag, resource.generators, i))
          // Add new el to tbody
          tbody.appendChild(cloned)
        }
      }
    })
  }
}

/**
 * Print
 *
 * @param {Element} element
 */
const print = (element: Element | null) => {
  if (!util.isUnset(element)) {
    // console.warn(element)
    printJS({printable: element, type: 'raw-html'})
    // Create a print window
    // const printWindow = window.open('', '', 'height=500, width=500');
    // printWindow.document.write(`<html lang="${env.get('language')}"><body>${element}</body></html>`);
    // printWindow.document.close();
    // printWindow.onload = () => printWindow.print()
  }
}

/**
 * Build template to actual HTML element
 *
 * @param {string} template
 * @param {[PrintTag]} tags
 * @param {object} data
 * @return {Promise<{readonly: {tags: array}, template: string, tags: array, data: object, generate: Promise<string>, generators: object, print: function()}>}
 */
const buildPrinter = (template: string, tags: PrintTag[], data: any): Promise<Printer> => {
  // Execute asynchronously
  return util.async(() => {
    // Build and return printer
    return new Printer(template, tags, data)
  });
}

export {buildPrinter, print}
