sap.ui.define(
    [
    "sap/ui/core/mvc/ControllerExtension",
    "sap/m/library"
    ], 
    function (ControllerExtension,library) {
        "use strict";
    
        return ControllerExtension.extend("books.controller.custom", {
            onRowPress: function(oContext) {
                this.base.editFlow
                .invokeAction("AdminService.openAttachment", {
                    contexts: oContext.getParameter("bindingContext")
                })
                .then(function (res) {
                    let odataurl = "";
                    if(res.getObject().value == "None") {
                        const lastSlashIndex = res.oModel.getServiceUrl().lastIndexOf('/');
                        let str = res.oModel.getServiceUrl();
                        if (lastSlashIndex !== -1) {
                            str = str.substring(0, lastSlashIndex)  + str.substring(lastSlashIndex + 1);
                        }
                        odataurl = str+res.oBinding.oContext.sPath+"/content";
                    } else {
                        odataurl = res.getObject().value;
                    }
                    library.URLHelper.redirect(odataurl, true);
                            
                });
            }
        });
    }
);