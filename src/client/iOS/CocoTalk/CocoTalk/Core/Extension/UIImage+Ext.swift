//
//  UIImage+Ext.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/10.
//

import Foundation
import UIKit

extension UIImage {
    
    func save(fileName: String) -> String? {
        let documentUrl = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        let fileURL = documentUrl.appendingPathComponent(fileName)
        if let imageData = self.jpegData(compressionQuality: 1.0) {
            try? imageData.write(to: fileURL, options: .atomic)
            return fileName
        }
        print("Error saving image")
        return nil
    }
    
    static func load(fileName: String) -> UIImage? {
        let documentUrl = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        let fileURL = documentUrl.appendingPathComponent(fileName)
        do {
            let imageData = try Data(contentsOf: fileURL)
            return UIImage(data: imageData)
        } catch {
            print("Error loading image : \(error)")
        }
        return nil
    }
    
    func resized(withPercentage percentage: CGFloat) -> UIImage? {
        let canvasSize = CGSize(width: size.width * percentage, height: size.height * percentage)
        UIGraphicsBeginImageContextWithOptions(canvasSize, false, scale)
        defer {
            UIGraphicsEndImageContext()
        }
        draw(in: CGRect(origin: .zero, size: canvasSize))
        return UIGraphicsGetImageFromCurrentImageContext()
    }
    
    
    /// 이미지 리사이즈
    /// 기본적으로 1024kB 이하로 압축한다.
    /// 변수 단위는 kB 입니다.
    func resized(to size: Double = 1024) -> UIImage? {
        guard let imageData = self.jpegData(compressionQuality: 1.0) else {
            return nil
        }
        
        var resizingImage = self
        var imageSizekB = Double(imageData.count) / size
        
        while imageSizekB > size {
            guard let resizedImage = resizingImage.resized(withPercentage: 0.9),
                  let imageData = resizedImage.jpegData(compressionQuality: 1.0) else {
                      return nil
                  }
            resizingImage = resizedImage
            imageSizekB = Double(imageData.count) / size
        }
        
        return resizingImage
    }
}
