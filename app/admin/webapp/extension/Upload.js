sap.ui.define(
    ["sap/m/MessageBox", "sap/m/MessageToast"],
    function (MessageBox, MessageToast) {
        "use strict";

        function _createUploadController(oExtensionAPI) {
            var oUploadDialog;

            function setOkButtonEnabled(bOk) {
                oUploadDialog && oUploadDialog.getBeginButton().setEnabled(bOk);
            }

            function setDialogBusy(bBusy) {
                oUploadDialog.setBusy(bBusy)
            }

            function closeDialog() {
                oUploadDialog && oUploadDialog.close()
            }

            function showError(sMessage) {
                MessageBox.error(sMessage || "Upload failed")
            }

            // TODO: Better option for this?
            function byId(sId) {
                return sap.ui.core.Fragment.byId("uploadDialog", sId);
            }

            return {
                onBeforeOpen: function (oEvent) {
                    oUploadDialog = oEvent.getSource();
                    oExtensionAPI.addDependent(oUploadDialog);
                },

                onAfterClose: function (oEvent) {
                    oExtensionAPI.removeDependent(oUploadDialog);
                    oUploadDialog.destroy();
                    oUploadDialog = undefined;
                },

                onOk: function (oEvent) {
                    setDialogBusy(true)

                    var oFileUploader = byId("uploader")

                    oFileUploader
                        .checkFileReadable()
                        .then(function () {
                            oFileUploader.upload();
                        })
                        .catch(function (error) {
                            showError("The file cannot be read.");
                            setDialogBusy(false)
                        })
                },

                onCancel: function (oEvent) {
                    closeDialog();
                },

                onTypeMismatch: function (oEvent) {
                    var sSupportedFileTypes = oEvent
                        .getSource()
                        .getFileType()
                        .map(function (sFileType) {
                            return "*." + sFileType;
                        })
                        .join(", ");

                    showError(
                        "The file type *." +
                        oEvent.getParameter("fileType") +
                        " is not supported. Choose one of the following types: " +
                        sSupportedFileTypes
                    );
                },

                onFileAllowed: function (oEvent) {
                    setOkButtonEnabled(true)
                },

                onFileEmpty: function (oEvent) {
                    setOkButtonEnabled(false)
                },

                onUploadComplete: function (oEvent) {
                    var iStatus = oEvent.getParameter("status");
                    var oFileUploader = oEvent.getSource()

                    oFileUploader.clear();
                    setOkButtonEnabled(false)
                    setDialogBusy(false)

                    if (iStatus >= 400) {
                        var oRawResponse = JSON.parse(oEvent.getParameter("responseRaw"));
                        showError(oRawResponse && oRawResponse.error && oRawResponse.error.message);
                    } else {
                        MessageToast.show("Uploaded successfully");
                        oExtensionAPI.refresh()
                        closeDialog();
                    }
                }
            };
        }

        return {
            showUploadDialog: function (oBindingContext, aSelectedContexts) {
                this.loadFragment({
                    id: "uploadDialog",
                    name: "admin.extension.UploadDialog",
                    controller: _createUploadController(this)
                }).then(function (oDialog) {
                    oDialog.open();
                });
            }
        };
    }
);