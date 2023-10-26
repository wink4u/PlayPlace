import styled from 'styled-components';
import css from 'styled-jsx/css';

interface TabbarItemWrapperProps {
	$isActive: boolean;
}

export const TabbarItemWrapper = styled.div<TabbarItemWrapperProps>`
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;

	span {
		font-size: 10px;
		background: ${({ $isActive }) => ($isActive ? 'var(--primary-grandiant-main)' : 'var(--white-700)')};
		-webkit-background-clip: text;
		-webkit-text-fill-color: transparent;
	}

	svg {
		width: 24px;
		height: 24px;

		path {
			fill: ${({ $isActive }) => ($isActive ? '' : 'var(--white-700)')};
		}
	}
`;
