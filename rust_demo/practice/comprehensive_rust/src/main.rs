#![allow(dead_code)] // 注意添加感叹号表示是一个crate级别的属性，否则为就近元素的属性
mod c1_basis;
mod c2_control_flow;
mod c3_variable;
mod c4_enum;
mod thread;

// https://google.github.io/comprehensive-rust/zh-CN/index.html
fn main() {
    thread::test::practice();
}
