import {boot} from 'quasar/wrappers'
import dayjs from "dayjs";
import quarterOfYear from "dayjs/plugin/quarterOfYear"

// *************************************************
// Implementation
// *************************************************

// Use Quarter
dayjs.extend(quarterOfYear)

export default boot(({app}) => {
  // @ts-ignore
  app.config.globalProperties.$dayjs = dayjs
})

export {dayjs}
