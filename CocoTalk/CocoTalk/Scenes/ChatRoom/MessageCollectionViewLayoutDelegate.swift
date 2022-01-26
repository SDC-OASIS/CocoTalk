//
//  MessageCollectionViewLayoutDelegate.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/26.
//

import Foundation
import UIKit

protocol MessageCollectionViewLayoutDelegate: AnyObject {
    func collectionView(_ collectionView: UICollectionView, heightForCellAtIndexPath indexPath: IndexPath) -> CGFloat
}
