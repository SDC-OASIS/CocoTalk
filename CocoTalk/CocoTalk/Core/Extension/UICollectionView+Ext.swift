//
//  UICollectionView+Ext.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/25.
//

import Foundation
import UIKit

extension UICollectionView {
    
    /// Last Section of the CollectionView
    var lastSection: Int {
        return numberOfSections - 1
    }
    
    /// IndexPath of the last item in last section.
    var lastIndexPath: IndexPath? {
        guard lastSection >= 0 else {
            return nil
        }
        
        let lastItem = numberOfItems(inSection: lastSection) - 1
        guard lastItem >= 0 else {
            return nil
        }
        
        return IndexPath(item: lastItem, section: lastSection)
    }
    
    /// Islands: Scroll to bottom of the CollectionView
    /// by scrolling to the last item in CollectionView
    func scrollToBottom(animated: Bool) {
        guard let lastIndexPath = lastIndexPath else {
            return
        }
        scrollToItem(at: lastIndexPath, at: .bottom, animated: animated)
    }
}
