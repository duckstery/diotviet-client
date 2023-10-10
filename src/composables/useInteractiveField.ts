import {util} from 'src/boot'

// *************************************************
// Typed
// *************************************************

export type UseInteractiveFieldResources = {
  onInteract(mode: string, data: string): void
}

// *************************************************
// Implementation
// *************************************************

/**
 * Use interactive field
 *
 * @return {{onInteract: function}}
 */
export function useInteractiveField(): UseInteractiveFieldResources {
  return {
    /**
     * On interact
     *
     * @param mode
     * @param data
     */
    onInteract: (mode, data) => {
      if (util.isUnset(util.nullIfEmpty(data))) {
        return;
      }

      if (mode === 'phoneNumber') {
        // Call number
        window.open(`tel:${data}`)
      } else if (mode === 'email') {
        // Open Gmail interface to compose email
        window.open(`https://mail.google.com/mail/?view=cm&fs=1&to=${data}`)
      } else if (mode === 'facebook') {
        // Open facebook link
        window.open(data)
      } else if (mode === 'address') {
        // Open Google Map link
        window.open(`https://www.google.com/maps/place/${encodeURIComponent(data)}`)
      }
    }
  }
}
