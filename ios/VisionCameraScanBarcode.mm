#import "VisionCameraScanBarcode.h"

#ifdef RCT_NEW_ARCH_ENABLED
#import "RNVisionCameraScanBarcodeSpec.h"
#endif

@implementation VisionCameraScanBarcode
RCT_EXPORT_MODULE()

RCT_REMAP_METHOD(scanCodeFromLibrary,
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)
{
  self.resolve = resolve;
  self.reject = reject;

  dispatch_async(dispatch_get_main_queue(), ^{
    UIImagePickerController *picker = [[UIImagePickerController alloc] init];
    picker.delegate = self;
    picker.allowsEditing = YES;
    picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
    [self presentViewController:picker animated:YES completion:nil];
  });
}

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info {
  UIImage *chosenImage = info[UIImagePickerControllerEditedImage];
  MLKBarcodeScanner *barcodeScanner = [MLKBarcodeScanner barcodeScanner];
  dispatch_async(dispatch_get_main_queue(), ^{
    [barcodeScanner processImage:chosenImage
                  completion:^(NSArray<MLKBarcode *> *_Nullable barcodes,
                                NSError *_Nullable error) {

      [picker dismissViewControllerAnimated:YES completion:[self waitAnimationEnd:^{
        if (error != nil) {
          // Error handling
          self.reject("Error...")
        }
        if (barcodes.count > 0) {
          // Recognized barcodes
          self.resolve(barcodes[0])
        }
      }]];
    }];
  });
}

// Don't compile this code when we build for the old architecture.
#ifdef RCT_NEW_ARCH_ENABLED
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeVisionCameraScanBarcodeSpecJSI>(params);
}
#endif

@end
