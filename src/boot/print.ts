import {loading, util} from "src/boot"
import printJS from 'print-js'
// @ts-ignore
import QRCode from "qrcode";
// @ts-ignore
import _ from "lodash"

// *************************************************
// Typed
// *************************************************

// Printer shape
export type IPrinter = {
  /**
   * Print tags
   */
  readonly tags: PrintTag[],
  /**
   * Data to print
   */
  data: any;
  /**
   * Whether Printer is ready to work
   */
  isReady: boolean,
  /**
   * Print template
   */
  template: string,
  /**
   * Content generator
   */
  generators: PrintGenerators | undefined,
  /**
   * Generate document
   */
  generate(): Promise<Element | null>,
  /**
   * Print document
   */
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
export type PrintGenerator = () => Promise<PrintGeneratorResource>
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
  private _isReady: boolean
  private readonly _logger: () => void

  readonly tags: PrintTag[]

  generators: PrintGenerators | undefined;
  template: string;

  /**
   * Constructors
   *
   * @param template
   * @param tags
   * @param logger
   */
  constructor(template: string, tags: PrintTag[], logger: (() => void) | null | undefined) {
    this._data = null
    this._isDirty = false
    this._element = null
    this._isReady = false
    this._logger = logger ?? (() => {
    })

    this.template = template
    this.tags = tags
    this.generators = undefined

    util.async(() => {
      // Build generators
      this.generators = this._buildGenerators()
      // Set ready flag
      this._isReady = true
    })
  }

  /**
   * Get isReady
   */
  get isReady() {
    return this._isReady
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
  async generate(): Promise<Element | null> {
    // Check if Printer is ready
    if (!this._isReady) {
      this._logger()
      return null
    }
    // Check if is not dirty, return cached element
    if (!this._isDirty && this._element !== null) {
      return this._element
    }

    // Generate a div body for template
    this._element = util.div(this.template)

    // Iterate through each simple printable tag in the printed Element
    for (const el of Array.from(this._element.querySelectorAll('.simple-tag'))) {
      await this._generateAndSetContentForElement(el, this.generators)
    }

    // Generate iterable tag
    await this._generateAndSetContentForWrappingTable(this._element, this.generators)

    // Mark as not dirty
    this._isDirty = false
    return this._element
  }

  /**
   * Print the printed Element
   */
  print(): void {
    // Check if Printer is ready
    if (!this._isReady) {
      this._logger()
      return
    }

    // @ts-ignore
    loading.wait(async () => print((await this.generate())?.innerHTML))
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
   * @param callback
   * @param size
   * @param generators
   */
  private _buildGenerator(callback: () => Promise<string>, size?: number, generators?: PrintGenerators): () => Promise<PrintGeneratorResource> {
    return async (): Promise<PrintGeneratorResource> => ({
      content: await callback(),
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
    // Save tag path at this method scope
    const path = tag.path
    // Return
    generators[tag.key] = this._buildGenerator(() => util.getProp(this._data, path))
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
        generators[subtag.key] = this._buildGenerator(async () => await this._generateBarcode(util.getProp(this._data, subtag.path)))
      } else if (subtag.key.includes('qr')) {
        // Check if using QR
        generators[subtag.key] = this._buildGenerator(async () => await this._generateQRCode(util.getProp(this._data, subtag.path)))
      } else {
        // Create normal generator
        generators[subtag.key] = this._buildGenerator(() => util.getProp(this._data, subtag.path))
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
    generators[tag.key] = async () => {
      // Deep clone tag to make sure changing readonly tag won't change
      const _tag: PrintTag = _.cloneDeep(tag)
      // Create resource
      const resource: PrintGeneratorResource = {
        // Get the size of actual data
        size: util.getProp(this._data, tag.path).length,
        // Sub generator
        generators: {}
      }
      // Template key, path
      let templateKey, templatePath = ''
      // Iterate through each subtag
      _tag.sub.forEach((subtag: PrintTag) => {
        // Save the key
        templateKey = subtag.key
        // Save the path
        templatePath = subtag.path
        // Loop in the size of "size"
        for (let i = 0; i < (resource.size ?? 0); i++) {
          // For each i,
          // Replace subtag key with a key that can be indexed
          subtag.key = templateKey + "_" + i
          // Replace the subtag.path with a corresponding path that point to data
          subtag.path = templatePath.replace('{i}', `${i}`)
          // Add generate for new subtag
          this._buildAndAddGenerators((resource.generators ?? {}), subtag)
        }
      })

      return resource
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
    // Check if generator for this key zis existing
    if (generators !== undefined && typeof generators[key] === 'function') {
      return generators[key]
    }

    return async () => {
      const output: PrintGeneratorResource = {content: undefined}
      return output
    }
  }

  /**
   * Generate <img/> for QRCode
   *
   * @param {string} content
   * @return {string}
   */
  private async _generateQRCode(content: string): Promise<string> {
    return `<div id="qrcode" title="${content}"><img src="${await QRCode.toDataURL(content, {width: 150})}" alt="Scan me!"></div>`
  }

  /**
   * Generate <img/> for Barcode
   *
   * @param {string} content
   * @return {string}
   */
  private async _generateBarcode(content: string): Promise<string> {
    // Create div container
    const canvas = document.createElement('canvas')
    // Create Barcode
    // @ts-ignore
    JsBarcode(canvas, content, {height: 50, font: 'Arial'})

    return `<div id="barcode" title="${content}"><img src="${canvas.toDataURL("image/png")}" alt="Scan me!"></div>`
  }

  /**
   * Generate content with generator and set content for Element
   *
   * @param element
   * @param generators
   * @param index
   */
  private async _generateAndSetContentForElement(element: Element, generators: PrintGenerators | undefined, index: number = -1) {
    // Pick generator by key
    const generator = this._getGeneratorForElement(element, generators, index)
    // Generate resource
    const resource = await generator()
    // Set el innerHTML with content.content
    element.innerHTML = resource.content ?? ''
  }

  /**
   * Handle iterable printable tag (wrapping table)
   *
   * @private
   */
  private async _generateAndSetContentForWrappingTable(element: Element, generators: PrintGenerators | undefined, times: number = 0) {
    // Break condition
    if (element === null || times === 5) return

    // Iterate through each wrapping-table, we'll presume that every wrapping-table will have a tbody
    for (const tbody of Array.from(element.querySelectorAll('.wrapping-table > tbody'))) {
      // Save childNodes (that is not a header) as template
      const templateElements = Array.from(tbody.children)
        .filter(el => util.isUnset(el.querySelector('th')) && !util.isUnset(tbody.removeChild(el)))
      // Pick generator by table.id (<tbody>'s parent) and generate content
      // @ts-ignore
      const resource = await generators[tbody.parentElement.id]()

      // Loop
      for (let i = 0; i < (resource.size ?? 0); i++) {
        // Iterate through each templated element
        for (const el of templateElements) {
          // Clone the el
          const cloned = <Element>el.cloneNode(true)
          // Query all .iterable-tag and iterate through each of them
          for (const tag of Array.from(cloned.querySelectorAll('.iterable-tag'))) {
            await this._generateAndSetContentForElement(tag, resource.generators, i)
          }

          // Add new el to tbody
          tbody.appendChild(cloned)
        }
      }
    }
  }
}

/**
 * Print
 *
 * @param {Element} element
 */
const print = (element: string | null) => {
  if (!util.isUnset(util.nullIfEmpty(element))) {
    printJS({printable: element, type: 'raw-html'})
  }
}

/**
 * Build template to actual HTML element
 *
 * @param {string} template
 * @param {[PrintTag]} tags
 * @param logger
 * @return {IPrinter}
 */
const buildPrinter = (template: string, tags: PrintTag[], logger: (() => void) | null | undefined = null): Printer => {
  return new Printer(template, tags, logger)
}

export {buildPrinter, print}
