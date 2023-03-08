import {computed} from "vue";
import {useOrderStore} from "stores/order";
import {util} from "boot/util";

/**
 * Proxy a field through computed
 *
 * @param {string|Array}field
 * @param {any} reference
 * @param {method} handler
 * @returns {Array}
 */
const proxy = (field, reference, handler) => {
  return (Array.isArray(field) ? [...field] : [field]).reduce((reduced, key) => {
    // Generate setter and getter
    reduced[key] = computed({
      get: () => reference[key],
      set: (value) => handler(value, key)
    })

    return reduced
  }, {})
}

export {proxy}

// setup: (props) => {
//   // Get store
//   const store = useOrderStore()
//
//   /**
//    * Edit item's event handler
//    *
//    * @param value
//    * @param key
//    */
//   const onEditItem = (value, key) => {
//     // Clone value
//     const clonedValue = Object.assign({}, props.value)
//     // Get bill reference
//     const bill = clonedValue.bill
//
//     // Set data of cloned value
//     bill[key] = value
//     // Calculate discountAmount
//     const discountAmount = bill['discountUnit'] === '%'
//       // Discount by percentage
//       ? util.toDec(bill['originalPrice']) / 100 * util.toDec(bill['discount'])
//       // Discount by plain value
//       : bill['discount']
//     // Calculate actualPrice
//     bill['actualPrice'] = `${util.toDec(bill['originalPrice']) - discountAmount}`
//     // Calculate totalPrice
//     bill['totalPrice'] = `${util.toDec(bill['actualPrice']) * util.toDec(bill['quantity'])}`
//
//     // Send request to update item's data
//     store.editItem(props.index, clonedValue)
//   }
//   // Destructuring
//
//   return {
//     ...proxy(['originalPrice', 'discount', 'discountUnit', 'quantity'], props.value.bill, onEditItem),
//   }
// },
