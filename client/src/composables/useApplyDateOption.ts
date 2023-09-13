import {Ref, ref, watch, WritableComputedRef} from "vue";

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
  // Backup
  const backup: Date = {from: null, to: null}
  // Picked option
  const option: Ref<Date> = ref('')
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
