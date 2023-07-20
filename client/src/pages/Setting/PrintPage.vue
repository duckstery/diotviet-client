<template>
  <Page :breadcrumbs="breadcrumbs">
    <div class="col-12 col-md-6 tw-px-2">
      <RichTextEditor v-model="content"/>
    </div>
    <div class="col-12 col-md-6 tw-px-2">
      <div v-html="content"></div>
    </div>
  </Page>
</template>

<script>
import Page from "components/General/Layout/Page.vue";
import RichTextEditor from "components/General/Other/RichTextEditor.vue";

import {ref} from "vue";
import Button from "components/General/Other/Button.vue";

export default {
  name: 'PrintPage',

  components: {Button, RichTextEditor, Page},

  setup() {
    return {
      content: ref('<div id="kv-cke-temp">\n' +
        '<div class="printBox">\n' +
        '<table style="width:100%">\n' +
        '\t<tbody>\n' +
        '\t\t<tr>\n' +
        '\t\t\t<td style="text-align:center">\n' +
        '\t\t\t<table style="width:100%">\n' +
        '\t\t\t\t<tbody>\n' +
        '\t\t\t\t\t<tr>\n' +
        '\t\t\t\t\t\t<td style="text-align:center"><span style="font-size:22px"><strong><span style="font-family:&quot;Times New Roman&quot;,Times,serif"><em>GIẶT SẤY NHANH&nbsp;</em></span></strong></span></td>\n' +
        '\t\t\t\t\t</tr>\n' +
        '\t\t\t\t\t<tr>\n' +
        '\t\t\t\t\t\t<td style="text-align:center"><strong><span style="font-size:12px">619 Nguyễn Trãi, P.11, Q. 5</span></strong></td>\n' +
        '\t\t\t\t\t</tr>\n' +
        '\t\t\t\t\t<tr>\n' +
        '\t\t\t\t\t\t<td style="font-size:11px; text-align:center"><span style="font-size:18px"><strong>Tel: 098.583.0556</strong></span></td>\n' +
        '\t\t\t\t\t</tr>\n' +
        '\t\t\t\t</tbody>\n' +
        '\t\t\t</table>\n' +
        '\t\t\t</td>\n' +
        '\t\t</tr>\n' +
        '\t</tbody>\n' +
        '</table>\n' +
        '\n' +
        '<table style="margin:5px 0 5px; width:100%">\n' +
        '\t<tbody>\n' +
        '\t\t<tr>\n' +
        '\t\t\t<td style="font-size:11px; text-align:center">{Ngay_Thang_Nam}</td>\n' +
        '\t\t</tr>\n' +
        '\t\t<tr>\n' +
        '\t\t\t<td style="font-size:11px; text-align:center"><span style="font-size:26px"><strong>{Khach_Hang}</strong></span><br />\n' +
        '\t\t\t<span style="font-size:24px">{So_Dien_Thoai}</span></td>\n' +
        '\t\t</tr>\n' +
        '\t\t<tr>\n' +
        '\t\t\t<td style="font-size:11px">&nbsp;</td>\n' +
        '\t\t</tr>\n' +
        '\t\t<tr>\n' +
        '\t\t\t<td style="font-size:11px"><span style="font-size:11px">Người lập phiếu:&nbsp;{Nhan_Vien_Ban_Hang}</span></td>\n' +
        '\t\t</tr>\n' +
        '\t</tbody>\n' +
        '</table>\n' +
        '\n' +
        '<table cellpadding="1" style="width:98%">\n' +
        '\t<tbody>\n' +
        '\t\t<tr>\n' +
        '\t\t\t<td style="border-bottom:1px solid black; border-top:1px solid black; width:35%"><span style="font-size:11px"><strong>Dịch vụ&nbsp;</strong></span></td>\n' +
        '\t\t\t<td style="border-bottom:1px solid black; border-top:1px solid black; text-align:right; width:30%"><span style="font-size:11px"><strong>SL</strong></span></td>\n' +
        '\t\t\t<td style="border-bottom:1px solid black; border-top:1px solid black; text-align:right"><span style="font-size:11px"><strong>T.Tiền</strong></span></td>\n' +
        '\t\t</tr>\n' +
        '\t\t<tr>\n' +
        '\t\t\t<td colspan="3" style="padding-top:3px"><span style="font-size:11px">{Ten_Hang_Hoa_Don_Gian}</span></td>\n' +
        '\t\t</tr>\n' +
        '\t\t<tr>\n' +
        '\t\t\t<td style="border-bottom:1px dashed black"><span style="font-size:11px">{Ghi_Chu_Hang_Hoa}</span></td>\n' +
        '\t\t\t<td style="border-bottom:1px dashed black; text-align:right"><span style="font-size:11px">{So_Luong}</span></td>\n' +
        '\t\t\t<td style="border-bottom:1px dashed black; text-align:right"><span style="font-size:11px">{Thanh_Tien}</span></td>\n' +
        '\t\t</tr>\n' +
        '\t</tbody>\n' +
        '</table>\n' +
        '\n' +
        '<table style="margin-top:px; width:100%">\n' +
        '\t<tbody>\n' +
        '\t\t<tr>\n' +
        '\t\t\t<td style="font-size:9px; font-style:italic; text-align:center"><span style="font-size:24px"><strong>{Tong_Tien_Hang} đ</strong></span></td>\n' +
        '\t\t</tr>\n' +
        '\t\t<tr>\n' +
        '\t\t\t<td style="font-size:9px; font-style:italic; text-align:center"><span style="font-size:11px"><strong>Điễm tích luỹ: {Tong_Diem_Sau_Hoa_Don}/10<br />\n' +
        '\t\t\t(10 LẦN tặng 1 LẦN)</strong></span></td>\n' +
        '\t\t</tr>\n' +
        '\t\t<tr>\n' +
        '\t\t\t<td style="font-size:11px; font-style:italic; text-align:center">Chân thành cảm ơn quý khách !</td>\n' +
        '\t\t</tr>\n' +
        '\t</tbody>\n' +
        '</table>\n' +
        '</div>\n' +
        '</div>\n')
    }
  },

  computed: {
    // Breadcrumbs
    breadcrumbs() {
      return [
        {label: this.$t('field.setting'), to: '/setting', icon: 'fa-gear'},
        {label: this.$t('field.print'), to: '/setting/print', icon: 'fa-print'},
      ]
    }
  },

  methods: {
    print() {

    }
  }
}
</script>
