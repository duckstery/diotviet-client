import {Dialog, DialogChainObject} from "quasar";
// @ts-ignore
import OrderProcessor from "components/Manage/Transaction/Order/OrderProcessor.vue";
import {Ref} from "@vue/reactivity";
import {notify, util} from "src/boot";
import {Printer} from "boot/print";
import {useI18n} from "vue-i18n";

/**
 * Setup Order processor
 *
 * @param {Ref | null} detailRef
 * @param {Printer | null} printer
 * @return {function(): DialogChainObject}
 */
export function useOrderProcessor(detailRef: Ref<{ code: string }> | null, printer: Printer | null = null): () => DialogChainObject {
  // Get $t
  const $t = useI18n().t
  // Setup printer
  if (printer === null) {
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
