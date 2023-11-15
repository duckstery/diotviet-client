import {onMounted, provide, shallowRef, ShallowRef} from "vue";
import {buildPrinter, IPrinter} from "boot/print";
import {LocalAxiosResponse} from "boot/axios";
import {axios, error, notify} from "src/boot";
import {useI18n} from "vue-i18n";
import {AxiosResponse} from "axios";

// *************************************************
// Typed
// *************************************************

export type UsePrinterResources = {
  printer: ShallowRef<IPrinter | null>,
  print(res: AxiosResponse<LocalAxiosResponse>): void,
}

// *************************************************
// Implementation
// *************************************************

/**
 * Setup Printer
 *
 * @param api
 * @param callback
 * @return {Ref<IPrinter>}
 */
export function usePrinter(api: string, callback: ((res: AxiosResponse<LocalAxiosResponse>) => void) | null | undefined = null): UsePrinterResources {
  // Get i18n
  const $t = useI18n().t
  // Create ShallowRef<IPrinter>
  const printer: ShallowRef<IPrinter | null> = shallowRef(null)

  // Const print method
  const print = (res: AxiosResponse<LocalAxiosResponse>) => {
    if (printer.value !== null) {
      // Set data for printer
      printer.value.data = res.data.payload
      // Print
      printer.value.print()
    }
  }
  // Provide printer for other component
  provide('printer', {print: print})

  // On mounted
  onMounted(() => {
    // Call API to get data for table
    axios.get(api)
      .then(res => {
        // Only call if callback is a function
        if (typeof callback === 'function') {
          // API callback
          callback(res)
        }

        // Build printer
        printer.value = buildPrinter(
          res.data.payload.template,
          res.data.payload.tags,
          {logger: () => notify($t('message.printer_preparing'), 'warning'),}
        )
      })
      .catch(error.any)
  })

  return {
    printer: printer,
    print: print,
  }
}
