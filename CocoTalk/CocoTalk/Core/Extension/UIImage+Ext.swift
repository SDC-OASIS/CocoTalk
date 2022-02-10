//
//  UIImage+Ext.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/10.
//

import Foundation
import UIKit

extension UIImage {
    
    func resized(withPercentage percentage: CGFloat) -> UIImage? {
        let canvasSize = CGSize(width: size.width * percentage, height: size.height * percentage)
        UIGraphicsBeginImageContextWithOptions(canvasSize, false, scale)
        defer {
            UIGraphicsEndImageContext()
        }
        draw(in: CGRect(origin: .zero, size: canvasSize))
        return UIGraphicsGetImageFromCurrentImageContext()
    }
    
    
    func resizedTo1mB() -> UIImage? {
        guard let imageData = self.jpegData(compressionQuality: 1.0) else {
            return nil
        }
        
        var resizingImage = self
        var imageSizekB = Double(imageData.count) / 1024.0
        
        while imageSizekB > 1024 {
            guard let resizedImage = resizingImage.resized(withPercentage: 0.9),
                  let imageData = resizedImage.jpegData(compressionQuality: 1.0) else {
                      return nil
                  }
            resizingImage = resizedImage
            imageSizekB = Double(imageData.count) / 1024.0
        }
        
        return resizingImage
    }
}
