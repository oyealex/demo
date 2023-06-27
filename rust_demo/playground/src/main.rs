fn main() {
    fn cache(input: &i32, sum: &mut i32) {
        *sum = *input + *input;
        assert_eq!(*sum, 2 * *input);
    }

// 清单 1-4: Rust 假设共享引用是不可变的。
}
