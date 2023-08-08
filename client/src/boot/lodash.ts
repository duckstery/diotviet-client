// @ts-ignore
import _ from 'lodash'
import {boot} from "quasar/wrappers";

export default boot(({app}) => {
  app.config.globalProperties.$_ = _
})
