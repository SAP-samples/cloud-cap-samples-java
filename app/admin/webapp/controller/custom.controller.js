sap.ui.define(
    [
    "sap/ui/core/mvc/ControllerExtension",
    "sap/m/library",
    "sap/ui/core/format/DateFormat"
    ], 
    function (ControllerExtension, library, DateFormat) {
        "use strict";
        const ChangeCategoryEnum = {
            created: "Created",
            updated: "Changed",
            security: "Access"
            // Add more mappings as needed
        };
    
        return ControllerExtension.extend("admin.controller.custom", {
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
            },
            onChangelogPress: function(oContext, aSelectedContexts) {
                var that =this;
                this.base.editFlow
                .invokeAction("AdminService.changelog", {
                    contexts: aSelectedContexts
                })
                .then(function (res) {
                    console.log("Result",res[0].value.getObject().value);
                    that.updateChangeLogInPropertiesModel(res[0].value.getObject().value);
                });
            },
            updateChangeLogInPropertiesModel: function (oChangeLogsForObjectResponse) {
                const aChangeLogs = [];
                const fileName = JSON.parse(oChangeLogsForObjectResponse).filename;
                console.log("Filename: ", fileName);
                const aChangeLogsObject = JSON.parse(oChangeLogsForObjectResponse)["changeLogs"];
                console.log("ChangeLogsObject:\n", aChangeLogsObject);
                // Take latest changes at the top
                for (let idx = aChangeLogsObject.length - 1; idx >= 0; idx--) {
                    const oChangeLogEntry = aChangeLogsObject[idx];
                    const sLastModifiedBy = oChangeLogEntry["user"];
                    const sChangeType = oChangeLogEntry["operation"];
                    const sChangeTime = oChangeLogEntry["time"];
                    let dateTimeFormat = DateFormat.getDateTimeInstance(sap.ui.getCore().getConfiguration().getLocale());
                    let changedDate = new Date(sChangeTime);
                    let changedTime = changedDate?dateTimeFormat.format(new Date(changedDate)) : "" ;
                    const oChangeLog = {
                        changedOn: changedTime,
                        changedBy: sLastModifiedBy,
                        changeType: ChangeCategoryEnum[sChangeType]
                    };
                    aChangeLogs.push(oChangeLog);
                    console.log("ChangeLog:\n", oChangeLog);
                }

                this.logFragment= this.base.getExtensionAPI().loadFragment({
                    name: "admin.fragments.changelog",
                    controller: this
                });
                var that = this;
                this.logFragment.then(function (dialog) {
                    if(dialog){
                        dialog.attachEventOnce("afterClose", function () {
                            dialog.destroy();
                        });
                        var oModel = new sap.ui.model.json.JSONModel();
                        oModel.setSizeLimit(100000);
                        oModel.setData(aChangeLogs);
                        that.getView().setModel(oModel, "changelog");
                        dialog.setTitle(fileName);
                        dialog.open()
                    }
                });
            },
            close: function (closeBtn) {
                closeBtn.getSource().getParent().close();
            }
        });
    }
);
