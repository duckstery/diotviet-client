// This is just an example,
// so you can safely delete all default props below

export default {
  // ****************************
  // Field
  // ****************************
  field: {
    new_order: 'New order',
    order: 'Order',
    general_price: 'General price',
    order_item: 'Order',
    purchase: 'Purchase',
    search_customer: 'Search for customer',
    categorize: 'Categorize',
    filter: 'Filter',
    visualize: 'Visualize',
    compactize: 'Compact',
    remove: 'Remove',
    more: 'More',
    quantity: 'Quantity',
    price: 'Price',
    total: 'Total',
    add: 'Add',
    edit_price: 'Edit price',
    save: 'Save',
    cancel: 'Cancel',
    original_price: 'Original price',
    discount: 'Discount',
    discount_by: 'By',
    actual_price: 'Actual price',
    note: 'Note',
    provisional_amount: 'Provisional amount',
    payment_amount: 'Payment amount',
    welcome: 'Welcome',
    login: 'Login',
    username: 'Username',
    password: 'Password',
    manage: 'Manage',
    sell: 'Sell',
    remember_password: 'Remember password',
    forget_password: 'Forget password',
    settings: 'Settings',
    setting: 'Setting',
    language: 'Language',
    en: 'English',
    vi: 'Vietnamese',
    display_mode: 'Display mode',
    light_mode: 'Light mode',
    dark_mode: 'Dark mode',
    logout: 'Logout',
    confirm: 'Confirm',
    confirm_cancel: 'Cancel',
    store: 'Store',
    product: 'Product',
    transaction: 'Transaction',
    partner: 'Partner',
    staff: 'Staff',
    outstream: 'Outstream',
    report: 'Report',
    list: 'List',
    receipt: 'Receipt',
    return: 'Return',
    customer: 'Customer',
    timekeeping: 'Timekeeping',
    home: 'Home',
    records_per_page: 'Records per page',
    display_col: 'Display column',
    true: 'True',
    false: 'False',
    expand: 'Expand',
    category: 'Category',
    group: 'Group',
    groups: 'Groups',
    all: 'All',
    price_range: 'Price range',
    can_be_accumulated: 'Can be accumulated',
    is_in_business: 'Is in business',
    collapse: 'Collapse',
    code: 'Code',
    measure_unit: 'Measure unit',
    edit: 'Edit',
    copy: 'Copy',
    start_business: 'Start business',
    stop_business: 'Stop business',
    start_accumulating: 'Start accumulating',
    stop_accumulating: 'Stop accumulating',
    delete: 'Delete',
    patch: 'Edit',
    history: 'History',
    description: 'Description',
    create: 'Create',
    update: 'Update',
    image: 'Image',
    src: 'Image',
    weight: 'Weight',
    title: 'Title',
    optimize: 'Optimize',
    visual: 'Visual',
    speed: 'Speed',
    operation: 'Operation',
    import: 'Import',
    export: 'Export',
    created_at: 'Created At',
    from_date: 'From date',
    to_date: 'To date',
    search: 'Search',
    birthday: 'Birthday',
    last_transaction: 'Last transaction',
    last_order_at: 'Last order at',
    last_transaction_at: 'Last transaction at',
    name: 'Name',
    gender: 'Gender',
    male: 'Male',
    female: 'Female',
    address: 'Address',
    phone_number: 'Phone',
    email: 'Email',
    facebook: 'Facebook',
    type: 'Type',
    resolved_at: 'Resolved at',
    pending: 'Pending',
    resolved: 'Resolved',
    aborted: 'Aborted',
    status: 'Status',
    from: 'From',
    to: 'To',
    created_by: 'Created by',
    point: 'Point',
    total_price: 'Total price',
    total_quantity: 'Total quantity',
    information: 'Information',
    processing: 'Processing',
    process: 'Process',
    print: 'Print',
    abort: 'Abort',
    resolve: 'Resolve',
    reason: 'Reason',
    date: 'Date',
    setup: 'Setup',
    component_tags: 'Component tags',
    template: 'Template',
    print_template: 'Print template',
    print_order: 'Order',
    print_receipt: 'Receipt',
    print_import: 'Import',
    print_export: 'Export',
    help: 'Help',
    reset: 'Reset',
    new: 'New',
    load: 'Load',

    role_1: 'Admin',
    role_2: 'Supervisor',
    role_3: 'Staff',
    role_4: 'Guest',
  },

  easter: {
    awesome: 'Yeah! Awesome~~~'
  },

  // ****************************
  // Entity
  // ****************************
  entity: {
    // ****************************
    // Special: Identifier
    // ****************************
    id_raw: "Identify number",
    id_bc: "Identify Barcode",
    id_qr: "Identify QR code",

    // ****************************
    // Entity: Product
    // ****************************
    product_id: 'ID',
    product_category: 'Product category',
    product_code: 'Product code',
    product_title: 'Product name',
    product_actualPrice: 'Actual price',
    product_measureUnit: 'Measure unit',
    product_weight: 'Weight',
    product_canBeAccumulated: 'Can be accumulated',
    product_isInBusiness: 'Is in business',

    // ****************************
    // Entity: Customer
    // ****************************
    customer_id: 'ID',
    customer_code: 'Customer code',
    customer_name: 'Customer name',
    customer_phoneNumber: 'Phone number',
    customer_email: 'Email',
    customer_address: 'Address',
    customer_birthday: 'Birthday',
    customer_isMale: 'Gender',
    customer_point: 'Point',
    customer_paymentAmount: 'Payment amount',
    customer_createdBy: 'Created by',
    customer_createdAt: 'Created at',
    customer_lastTransactionAt: 'Last transaction',
    customer_lastOrderAt: 'Last order at',

    // ****************************
    // Entity: Order
    // ****************************
    order_id: 'ID',
    order_code: 'Order code',
    order_customer: 'Customer name',
    order_phoneNumber: 'Phone number',
    order_address: 'Address',
    order_paymentAmount: 'Payment amount',
    order_status: 'Status',
    order_point: 'Point',
    order_createdBy: 'Created by',
    order_createdAt: 'Created at',
    order_resolvedAt: 'Resolved at',
    order_items: 'Order items',
    order_provisionalAmount: 'Provisional amount',
    order_discount: 'Discount',
    order_discountUnit: 'Discount unit',
    order_note: 'Note',

    // ****************************
    // Entity: Item
    // ****************************
    item_title: 'Product name',
    item_originalPrice: 'Original price',
    item_discount: 'Discount',
    item_discountUnit: 'Discount unit',
    item_actualPrice: 'Actual price',
    item_totalPrice: 'Total price',
    item_note: 'Note',
    item_quantity: 'Quantity',
  },

  // ****************************
  // Message
  // ****************************
  message: {
    please_wait: 'Please kindly wait...',
    confirm: 'Are you sure you want to {attr}?',
    reason_confirm: 'Please input reason',
    table_empty_data: 'Well this is sad... I didn\'t find anything for you',
    rows_selected: '{attr} record(s) selected.',
    from_to: 'From {from} to {to}',
    pick_file: 'Drop file or browse',
    invalid_file: 'Too big or invalid file(s)',
    blank_for_auto: 'Leave this field blank for auto creation',
    required: 'This field is required',
    invalid_input: 'Invalid input',
    success: '{attr} successfully',
    fail: '{attr} failed',
    from: 'From {attr}',
    inconsistent_data: 'Data has been refreshed because of the inconsistency',
    numeric: 'Only allow numeric value',
    email: 'This is not a valid email',
    specify_customer: 'Please specify a customer',
    specify_least_item: 'Please specify at least 1 item',
    no_recent_searches: 'No recent searches',
    search_orders: 'Search orders',
    group_by: 'Group by {attr}',
    delete_order: 'Delete this order',
    delete_item: 'Delete this item',
    action_on_item: '{attr} this item',
    import_file: 'Import this file',
    export_data: 'Export this data',
    order_create: 'Order create successfully',
    action_on_order: '{attr} this order',
    invalid_iterable_tag: 'This tag must be placed in a table',
    invalid_iterable_area: 'Child tags of [{attr}] must be placed in the same table',
    invalid_iterable_area_level: 'This tag must be placed in level {attr} above table',
    invalid_child_iterable_placement: 'This tag must be places inside a table that inside [{attr}]',
    existed_tag: 'This tag is already exists',
    max_length: 'Maximum character is {max}',
    save_document: 'Save this document',
    delete_document: 'Delete this document',
    least_document: 'You must have at least 1 document',
    print_unavailable: 'Printing is unavailable here',
    printer_preparing: 'Please wait while printer is being prepared'
  },

  // ****************************
  // Error
  // ****************************
  error: {
    limit_exceeded: 'Total size has exceeded maximum limit',
    status_403: 'You are not authorized to enter this page. Please go back.',
    status_404: 'Oops. Nothing here...',
  }
}
