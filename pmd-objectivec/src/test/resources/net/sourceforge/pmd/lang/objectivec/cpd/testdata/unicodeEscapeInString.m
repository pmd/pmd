@property (nonatomic, strong) UIButton *copyrightsButton;

- (void)setupCopyrightsButton {
    self.copyrightsButton = [[UIButton alloc] initWithFrame:CGRectZero];
    [self.copyrightsButton setTitle:@"\u00a9" forState:UIControlStateNormal];
}

@end
