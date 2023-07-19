import {Dialog} from "quasar";
import OrderProcessor from "components/Manage/Transaction/Order/OrderProcessor.vue";

/**
 * Setup Order processor
 *
 * @param {ComputedRef} detailRef
 * @return {function(): DialogChainObject}
 */
export function useOrderProcessor(detailRef) {
  // Invoke dialog
  return () => Dialog.create({
    component: OrderProcessor,
    componentProps: {selectedCode: detailRef.value.code}
  })
}
