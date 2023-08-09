import {Dialog, DialogChainObject} from "quasar";
import OrderProcessor from "components/Manage/Transaction/Order/OrderProcessor.vue";
import {Ref} from "@vue/reactivity";
import {util} from "src/boot";

/**
 * Setup Order processor
 *
 * @param {ComputedRef} detailRef
 * @return {function(): DialogChainObject}
 */
export function useOrderProcessor(detailRef: Ref<{code: string}> | null | undefined): () => DialogChainObject {
  console.warn(detailRef)
  // Invoke dialog
  return () => Dialog.create({
    component: OrderProcessor,
    componentProps: {selectedCode: util.isUnset(detailRef) ? null : detailRef.value.code}
  })
}
