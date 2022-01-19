//
//  UIImageView+Ext.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/19.
//

import UIKit

extension UIImageView {
    func setShadow() {
        self.layer.shadowColor = UIColor.black.cgColor
        self.layer.shadowRadius = 3.0
        self.layer.shadowOpacity = 1.0
        self.layer.shadowOffset = CGSize(width: 0, height: 4)
        self.layer.masksToBounds = false
    }
}
