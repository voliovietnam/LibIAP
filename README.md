### Init
In MyApplication:
 ```xml
     IapConnector.initIap(this@MyApplication,"iap_id.json")
 ```
File: iap_id.json
 ```xml
 {
   "id": "iapmonthly",
   "type": "subs"
 },
 {
   "id": "iapforever",
   "type": "inapp"
 }
]
```

### CheckPurchasesIap
 ```xml
IapConnector.isPurchasesIap.observe(viewLifecycleOwner) {
           it?.let {
               if(it){
                   // DA MUA IAP
               } else{
                   // CHUA MUA IAP
               }
           }
       }
```

### Buy
 ```xml
IapConnector.buyIap(activity,"iapforever")
```

### Reset Iap
 ```xml
IapConnector.resetIap(activity)
```

### AddListenerSub 
 ```xml
  IapConnector.addIAPListener(object : SubscribeInterface{
            override fun subscribeSuccess(productModel: ProductModel) {
                TODO("Not yet implemented")
            }

            override fun subscribeError(error: String) {
                TODO("Not yet implemented")
            }

        })

```
### RemoteListenerSub
```xml
 IapConnector.removeIAPListener(object : SubscribeInterface)
 ```
 
### GetAllIapProduct
```xml
 IapConnector.getAllProductModel()
```
