#![allow(dead_code)] // 注意添加感叹号表示是一个crate级别的属性，否则为就近元素的属性
mod c1_basis;
mod c2_control_flow;
mod c3_variable;
mod c4_enum;

// https://google.github.io/comprehensive-rust/zh-CN/index.html
fn main() {
    // c3_variable::variable_type::practice();
    // c3_variable::static_and_const::practice();
    // c4_enum::payload::practice();
    c4_enum::layout::practice();
}
