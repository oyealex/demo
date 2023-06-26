#[cfg(test)]
mod test {
    use std::cell::RefCell;
    use std::rc::Rc;

    // Definition for a binary tree node.
    #[derive(Debug, PartialEq, Eq)]
    pub struct TreeNode {
        pub val: i32,
        pub left: Option<Rc<RefCell<TreeNode>>>,
        pub right: Option<Rc<RefCell<TreeNode>>>,
    }

    impl TreeNode {
        #[inline]
        pub fn new(val: i32) -> Self {
            TreeNode {
                val,
                left: None,
                right: None,
            }
        }
    }

    #[test]
    // https://leetcode.cn/problems/univalued-binary-tree/
    fn no_965() {
        fn is_unival_tree(root: Option<Rc<RefCell<TreeNode>>>) -> bool {
            fn visit_and_match(root: Option<Rc<RefCell<TreeNode>>>, target: i32) -> bool {
                if let Some(r) = root {
                    r.borrow().val == target
                        && visit_and_match(r.borrow().left.clone(), target)
                        && visit_and_match(r.borrow().right.clone(), target)
                } else {
                    true
                }
            }

            let target = root.clone().unwrap().borrow().val;
            visit_and_match(root, target)
        }

    }
}
