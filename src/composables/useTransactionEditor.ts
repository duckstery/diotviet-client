import {Dialog, DialogChainObject} from "quasar";
// @ts-ignore
import TransactionEditor from "components/Manage/Transaction/TransactionEditor.vue";

/**
 * Setup TransactionEditor
 */
export function useTransactionEditor(): () => DialogChainObject {
  // Invoke dialog
  return () => Dialog.create({
    component: TransactionEditor,
  })
}
