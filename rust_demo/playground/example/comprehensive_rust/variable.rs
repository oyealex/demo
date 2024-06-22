pub mod variable_type {
    pub fn practice() {
        let v = vec![(10, false), (20, true)];
        println!("v: {v:?}");

        // 自动推导类型
        let vv = v.iter().collect::<std::collections::HashSet<_>>();
        println!("vv: {vv:?}");
    }
}

pub mod static_and_const {
    const DIGEST_SIZE: usize = 3;
    const ZERO: u8 = 42;

    pub fn practice() {
        let text = "Hello";
        let mut digest = [ZERO; DIGEST_SIZE];
        for (idx, &b) in text.as_bytes().iter().enumerate() {
            digest[idx % DIGEST_SIZE] = digest[idx % DIGEST_SIZE].wrapping_add(b);
        }
        println!("digest: {digest:?}");
    }
}
