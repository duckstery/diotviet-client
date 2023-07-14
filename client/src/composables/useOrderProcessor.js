import {Dialog} from "quasar";
import OrderEditor from "components/Manage/Transaction/Order/OrderEditor.vue";

/**
 * Setup Order processor
 */
export function useOrderProcessor(orderId) {
  // Invoke dialog
  return () => Dialog.create({
    component: OrderEditor,
    componentProps: {selectedId: orderId}
  })
}
