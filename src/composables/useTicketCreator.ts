import {useI18n} from "vue-i18n";
import {axios, buildPrinter, error, notify, util} from "src/boot";
import {toRaw} from "vue";
import {useAdvanceStorage} from "src/composables/useAdvanceStorage";
import {useTicketStore} from "stores/ticket";
import {Ref} from "@vue/reactivity";

/**
 * Setup Ticket creator
 */
export function useTicketCreator(customerRef: Ref): () => void {
  // Get i18n
  const $t = useI18n().t
  // Use advance store on Ticket init
  const ticketStore = useAdvanceStorage(
    useTicketStore,
    async () => (await axios.get('/ticket/init')).data.payload
  )
  // Build printer
  const printer = buildPrinter(
    ticketStore.template,
    ticketStore.tags,
    {
      logger: () => notify($t('message.printer_preparing'), 'warning'),
      qrWidth: 300,
      bcHeight: 200
    }
  )

  // Invoke dialog
  return () => {
    // Notify if no customer
    if (util.isUnset(customerRef.value)) {
      return notify($t("message.specify_customer"), 'negative');
    }
    axios.post('/ticket/store', customerRef.value)
      .then(res => {
        // Set data for printer
        printer.data = {...customerRef.value, value: res.data.payload}
        // Print
        printer.print()
      })
      .catch(error.any)
  }
}
