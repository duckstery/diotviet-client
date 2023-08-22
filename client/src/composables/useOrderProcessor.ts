import {Dialog, DialogChainObject} from "quasar";
// @ts-ignore
import OrderProcessor from "components/Manage/Transaction/Order/OrderProcessor.vue";
import {Ref} from "@vue/reactivity";
import {notify, util} from "src/boot";
import {useI18n} from "vue-i18n";
import {inject} from "vue";

/**
 * Setup Order processor
 *
 * @param {Ref | null} detailRef
 * @param printerInjectKey
 * @return {function(): DialogChainObject}
 */
export function useOrderProcessor(detailRef: Ref<{ code: string }> | null, printerInjectKey: string = 'printer'): () => DialogChainObject {
  // Get $t
  const $t = useI18n().t
  // Try to inject printer
  let printer = inject(printerInjectKey)
  if (util.isUnset(printer)) {
    // @ts-ignore
    printer = {print: () => notify($t('message.print_unavailable'), 'warning')}
  }

  // Invoke dialog
  return () => Dialog.create({
    component: OrderProcessor,
    // @ts-ignore
    componentProps: {selectedCode: util.isUnset(detailRef) ? null : detailRef.value.code, printer: printer}
  })
}
