//
//  UIViewController+Ext.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/10.
//

import Foundation
import UIKit

extension UIViewController {
    func switchRoot(to root: UIViewController) {
        setNeedsStatusBarAppearanceUpdate()
        view.window?.rootViewController = root
        view.window?.makeKeyAndVisible()
    }
}
