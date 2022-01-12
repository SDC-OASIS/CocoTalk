//
//  UIButton+Ext.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/12.
//

import UIKit

extension UIButton {
    open override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        super.touchesBegan(touches, with: event)
        super.superview?.endEditing(true)
    }
}
