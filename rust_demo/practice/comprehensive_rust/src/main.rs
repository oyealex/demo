#![allow(dead_code)] // 注意添加感叹号表示是一个crate级别的属性，否则为就近元素的属性
mod c1_basis;
mod c2_control_flow;

// https://google.github.io/comprehensive-rust/zh-CN/index.html
fn main() {
    c1_basis::basis_types::practice();
    // control_flow::block::practice();
    // control_flow::if_expression::practice();
    // control_flow::for_loop::practice();
    // control_flow::while_loop::practice();
    // control_flow::break_and_continue::practice();
    // c2_control_flow::loop_expression::practice();
}
