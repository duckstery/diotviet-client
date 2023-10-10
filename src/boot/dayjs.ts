import {boot} from 'quasar/wrappers'
import dayjs from "dayjs";
import quarterOfYear from "dayjs/plugin/quarterOfYear"
import customParseFormat from "dayjs/plugin/customParseFormat"

// *************************************************
// Implementation
// *************************************************

// Use Quarter
dayjs.extend(quarterOfYear)
dayjs.extend(customParseFormat)

export default boot(({app}) => {
  // @ts-ignore
  app.config.globalProperties.$dayjs = dayjs
})

export {dayjs}
