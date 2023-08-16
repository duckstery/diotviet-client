import {Dialog, DialogChainObject} from "quasar";
// @ts-ignore
import OrderProcessor from "components/Manage/Transaction/Order/OrderProcessor.vue";
import {Ref} from "@vue/reactivity";
import {util} from "src/boot";

/**
 * Setup Order processor
 *
 * @param {ComputedRef} detailRef
 * @return {function(): DialogChainObject}
 */
export function useOrderProcessor(detailRef: Ref<{ code: string }> | null): () => DialogChainObject {
  // Invoke dialog
  return () => Dialog.create({
    component: OrderProcessor,
    // @ts-ignore
    componentProps: {selectedCode: util.isUnset(detailRef) ? null : detailRef.value.code}
  })
}
