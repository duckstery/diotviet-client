import {ref, onMounted} from "vue";

/**
 * Setup max width class
 *
 * @param {function} provider
 * @param {number} scaleDownRate
 * @return {Ref<string>}
 */
export function useMaxWidth(provider, scaleDownRate= 0.5) {
  // Create class ref
  const twClass = ref('')

  onMounted(() => {
    // Scaled down width
    const width = Math.ceil(provider().getBoundingClientRect().width * scaleDownRate)
    // Setup
    twClass.value = `tw-max-w-[${width}px]`
  })

  return twClass
}
