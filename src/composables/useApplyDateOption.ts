import {Ref, ref, watch, WritableComputedRef} from "vue";
import {util} from "src/boot";

// *************************************************
// Typed
// *************************************************

export type Date = null | string | { from: string | null, to: string | null }

export type UseApplyDateOptionResources = {
  option: Ref<Date>,
  applyModel(): void
}

// *************************************************
// Implementation
// *************************************************

/**
 * Apply Date value to model
 *
 * @param fromModel
 * @param toModel
 */
export function useApplyDateOption(fromModel: WritableComputedRef<any>, toModel: WritableComputedRef<any>): UseApplyDateOptionResources {
  // Lazy method
  const isEmpty = (value: any) => util.isUnset(util.nullIfEmpty(value))
  // Backup
  const backup: Date = {from: fromModel.value, to: toModel.value}
  // Picked option
  const option: Ref<Date> = ref(isEmpty(fromModel.value) && isEmpty(toModel.value) ? null : {from: fromModel.value, to: toModel.value})
  // Watch for option changed
  watch(option, (value: Date) => {
    // Set model
    backup.from = typeof value === 'string' || value === null ? value : value.from
    backup.to = typeof value === 'string' || value === null ? value : value.to

    // Apply model
    applyModel()
  })

  // Apply model manually
  const applyModel = () => {
    fromModel.value = backup.from
    toModel.value = backup.to
  }

  // ChartJs complete options
  return {option: option, applyModel: applyModel}
}
