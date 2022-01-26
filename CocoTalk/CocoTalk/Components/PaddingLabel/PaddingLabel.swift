//
//  PaddingLabel.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/24.
//

import UIKit

class PaddingLabel: UILabel {
    
    var topInset: CGFloat = 8
    var bottomInset: CGFloat = 8
    var leftInset: CGFloat = 10
    var rightInset: CGFloat = 10
    
    override func drawText(in rect: CGRect) {
        let insets = UIEdgeInsets(top: topInset, left: leftInset, bottom: bottomInset, right: rightInset)
        super.drawText(in: rect.inset(by: insets))
    }
    
    override var intrinsicContentSize: CGSize {
        let size = super.intrinsicContentSize
        return CGSize(width: size.width + leftInset + rightInset, height: size.height + topInset + bottomInset)
    }
    
    override var bounds: CGRect {
        didSet { preferredMaxLayoutWidth = bounds.width - (leftInset + rightInset) }
    }
}
