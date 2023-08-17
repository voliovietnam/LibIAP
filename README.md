### implementation
```xml
implementation 'com.github.voliovietnam:LibIAP:1.0.1'
```

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
IapConnector.listPurchased.observe(viewLifecycleOwner) {
           it?.let {
                CoroutineScope(Dispatchers.Main).launch {
                    if (it.isNotEmpty()) {
                      //da mua
                    } else {
                        //chua mua
                    }
                }
            }
       }
```

### Buy
 ```xml
   IapConnector.buyIap(
        activity: Activity,
        productId: String,
        typeSub: TypeSub = TypeSub.Base,
    ) 
```

### BuyUpgrade
 ```xml
IapConnector.buyIapUpgrade(activity: Activity, productId: String, productIdOlder: String)
```

### PercentSale
 ```xml
 IapConnector.percentSale(productId: String)
```

### TypeIap
 ```xml
 IapConnector.typeIap(productId: String)
```

### IapInformation
 ```xml
 IapConnector.iapInformation(productId: String)
```

### ListSubInformation
 ```xml
 IapConnector.listSubInformation(productId: String)
```

### SubInformation
 ```xml
 IapConnector.subInformation(productId: String, typeSub: TypeSub)
```

### SubSaleInformation
 ```xml
 IapConnector.subSaleInformation(productId: String)
```

### SubTrailInformation
 ```xml
 IapConnector.subTrailInformation(productId: String)
```

### SubBaseInformation
 ```xml
 IapConnector.subBaseInformation(productId: String)
```

### InAppInformation
 ```xml
 IapConnector.inAppInformation(productId: String)
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
