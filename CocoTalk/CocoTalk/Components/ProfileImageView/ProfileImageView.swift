//
//  ProfileImageView.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/17.
//

import UIKit

class ProfileImageView: UIImageView {

    private let ivMask = UIImageView(image: UIImage(named: "squircle_mask")!)
    
    override init(image: UIImage?) {
        super.init(image: image)
        self.mask = ivMask
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        ivMask.frame = self.bounds
    }
    
}
