'use client';

import { PropsWithChildren, useEffect } from 'react';
import { RecoilRoot } from 'recoil';
import { usePathname, useRouter } from 'next/navigation';
import StompClientProvider from '@/components/atoms/StompClientProvider/StompClientProvider';
import GlobalStyles from '../styles/GlobalStyles';

function Providers({ children }: PropsWithChildren) {
	const router = useRouter();
	const pathname = usePathname();

	useEffect(() => {
		if (!localStorage.getItem('accessToken') && pathname !== '/login' && pathname !== '/signup') {
			// alert('로그인이 필요합니다.');
			// router.push('/login');
		}
	}, [pathname, router]);

	return (
		<>
			<RecoilRoot>
				<GlobalStyles />
				<StompClientProvider>{children}</StompClientProvider>
			</RecoilRoot>
		</>
	);
}

export default Providers;
