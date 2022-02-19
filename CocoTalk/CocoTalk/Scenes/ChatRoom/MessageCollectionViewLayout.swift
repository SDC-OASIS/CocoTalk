//
//  MessageCollectionViewLayout.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/26.
//

import UIKit

final class MessageCollectionViewLayout: UICollectionViewLayout {
    weak var delegate: MessageCollectionViewLayoutDelegate?
    
    fileprivate var cache: [UICollectionViewLayoutAttributes] = []
    fileprivate var contentHeight: CGFloat = 0.0
    fileprivate var cellPadding: CGFloat = 4.0
    
    fileprivate var contentWidth: CGFloat {
        guard let collectionView = collectionView else {
            return 0.0
        }
        let insets = collectionView.contentInset
        return collectionView.bounds.width - (insets.left + insets.right)
    }
    
    override var collectionViewContentSize: CGSize {
        return CGSize(width: contentWidth, height: contentHeight)
    }
    
    override func prepare() {
        super.prepare()
        
        cache = []
        
        guard let collectionView = collectionView else {
                  return
              }
        
        // xOffset 계산
        let columnWidth: CGFloat = contentWidth
        let xOffset: CGFloat = 0
        
        // yOffset 계산
        var yOffset: CGFloat = 0
        for item in 0..<collectionView.numberOfItems(inSection: 0) {
            let indexPath = IndexPath(item: item, section: 0)
            let cellHeight = delegate?.collectionView(collectionView, heightForCellAtIndexPath: indexPath) ?? 0
            let height = cellPadding * 2 + cellHeight
            let frame = CGRect(x: xOffset, y: yOffset, width: columnWidth, height: height)
            let insetFrame = frame.insetBy(dx: 0, dy: cellPadding)
            
            // cache 저장
            let attributes = UICollectionViewLayoutAttributes(forCellWith: indexPath)
            attributes.frame = insetFrame
            cache.append(attributes)
            
            // 새로 계산된 항목의 프레임을 설명하도록 확장
            contentHeight = max(contentHeight, frame.maxY)
            yOffset = yOffset + height
        }
    }
    
    override func layoutAttributesForElements(in rect: CGRect) -> [UICollectionViewLayoutAttributes]? {
        return cache.filter { rect.intersects( $0.frame) }
    }
    
    override func layoutAttributesForItem(at indexPath: IndexPath) -> UICollectionViewLayoutAttributes? {
        return cache[indexPath.item]
    }
}
